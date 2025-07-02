package tmoney.gbi.bms.common.converter;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import tmoney.gbi.bms.common.crypto.CryptoService;
import tmoney.gbi.bms.proto.Options;

import java.lang.reflect.Method;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenericProtobufConverterFactory implements ConverterFactory<byte[], Message> {

    private final CryptoService cryptoService;

    @Override
    @NonNull
    public <T extends Message> Converter<byte[], T> getConverter(@NonNull Class<T> targetType) {
        return new ProtobufConverter<>(targetType, cryptoService);
    }

    private static class ProtobufConverter<T extends Message> implements Converter<byte[], T> {
        private final Class<T> targetType;
        private final CryptoService cryptoService;

        public ProtobufConverter(Class<T> targetType, CryptoService cryptoService) {
            this.targetType = targetType;
            this.cryptoService = cryptoService;
        }

        @Override
        public T convert(@NonNull byte[] source) {
            if (source.length == 0) {
                return null;
            }
            try {
                Descriptors.Descriptor targetDescriptor = (Descriptors.Descriptor) targetType.getMethod("getDescriptor").invoke(null);
                DescriptorProtos.MessageOptions options = targetDescriptor.getOptions();
                String sourceClassName = options.getExtension(Options.transformSource);

                if (!sourceClassName.isEmpty()) {
                    return transformAndEncrypt(source, sourceClassName, targetDescriptor);
                } else {
                    return parseDirectly(source);
                }

            } catch (Exception e) {
                log.error("Protobuf conversion failed for target type: {}", targetType.getName(), e);
                throw new IllegalStateException("Protobuf conversion failed", e);
            }
        }

        private T parseDirectly(byte[] source) throws Exception {
            Method parseFromMethod = targetType.getMethod("parseFrom", byte[].class);
            @SuppressWarnings("unchecked")
            T result = (T) parseFromMethod.invoke(null, (Object) source);
            return result;
        }

        private T transformAndEncrypt(byte[] source, String sourceClassName, Descriptors.Descriptor targetDescriptor) throws Exception {
            Class<?> sourceClass = Class.forName(sourceClassName);
            Method parseFromMethod = sourceClass.getMethod("parseFrom", byte[].class);
            Message sourceMessage = (Message) parseFromMethod.invoke(null, (Object) source);

            Message.Builder targetBuilder = (Message.Builder) targetType.getMethod("newBuilder").invoke(null);

            for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : sourceMessage.getAllFields().entrySet()) {
                Descriptors.FieldDescriptor sourceField = entry.getKey();
                Object sourceValue = entry.getValue();
                Descriptors.FieldDescriptor targetField = targetDescriptor.findFieldByName(sourceField.getName());

                if (targetField == null) continue;

                boolean needsEncryption = targetField.getOptions().getExtension(Options.encrypt);

                if (needsEncryption) {
                    String encryptedValue = cryptoService.encrypt(String.valueOf(sourceValue));
                    targetBuilder.setField(targetField, encryptedValue);
                } else {
                    targetBuilder.setField(targetField, sourceValue);
                }
            }

            @SuppressWarnings("unchecked")
            T result = (T) targetBuilder.build();
            return result;
        }
    }
}