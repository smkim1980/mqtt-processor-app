package tmoney.gbi.bms.handler;

import com.github.tocrhz.mqtt.annotation.MqttSubscribe;
import com.github.tocrhz.mqtt.annotation.Payload;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import tmoney.gbi.bms.common.crypto.CryptoService;
import tmoney.gbi.bms.proto.EncryptedLocation;
import tmoney.gbi.bms.proto.Location;

import static tmoney.gbi.bms.common.constant.MqttTopicConstants.OBE_TBUS_INB_TOPIC;
import static tmoney.gbi.bms.common.constant.TopicRuleNames.QOS_1;

@Component
@Slf4j
@RequiredArgsConstructor
public class MqttMessageHandler {

    private final CryptoService cryptoService;

    @MqttSubscribe(value = OBE_TBUS_INB_TOPIC, qos = QOS_1)
    public void handleLocation(String topic,
                               @Payload EncryptedLocation location) {

        log.info("--- [Handler] Message Received on Topic: {} ---", topic);

        if (location != null) {
            log.info("[Handler] Transformed Encrypted Latitude: '{}'", location.getLatitude());
            log.info("[Handler] Transformed Encrypted Longitude: '{}'", location.getLongitude());

            String decryptedLat = cryptoService.decrypt(location.getLatitude());
            String decryptedLon = cryptoService.decrypt(location.getLongitude());
            log.info("[Handler] Decrypted Latitude: '{}'", decryptedLat);
            log.info("[Handler] Decrypted Longitude: '{}'", decryptedLon);
        } else {
            log.error("[Handler] EncryptedLocation transformation failed. Topic: {}", topic);
        }
    }

    private void handlerLocationsData(String topic, byte[] payload) {
        try {
            Location locationData = Location.parseFrom(payload);
            log.info("######################### topic : {} , locationData :{}", topic, locationData.toString());
        } catch (InvalidProtocolBufferException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }
}