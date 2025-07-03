package tmoney.gbi.bms.sample;

import com.github.tocrhz.mqtt.publisher.MqttPublisher;
import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tmoney.gbi.bms.model.LocationDto;
import tmoney.gbi.bms.proto.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MqttProtobufPublisherService {

    private final MqttPublisher publisher;

    /**
     * BMS 데이터를 Protobuf 메시지로 발송
     */
    public void publishLocationData(LocationDto dto) {
        try {
            Location.Builder builder = Location.newBuilder();

            // 기본 타입 필드 설정
            builder.setLatitude(dto.getLatitude());
            builder.setLongitude(dto.getLongitude());
            builder.setHeading(dto.getHeading());
            builder.setAltitude(dto.getAltitude());
            builder.setLastStopDist((int) dto.getLastStopDist()); // uint32 캐스팅
            builder.setOriginAccumDist((int) dto.getOriginAccumDist()); // uint32 캐스팅

            // Speed 중첩 메시지 변환
            if (dto.getSpeed() != null) {
                builder.setSpeed(Speed.newBuilder().setValue(dto.getSpeed().getValue()).build());
            }

            // Timestamp 변환 (Instant -> Timestamp)
            if (dto.getOccurAt() != null) {
                Timestamp timestamp = Timestamp.newBuilder()
                        .setSeconds(dto.getOccurAt().getEpochSecond())
                        .setNanos(dto.getOccurAt().getNano())
                        .build();
                builder.setOccurAt(timestamp);
            }

            // Enum 변환
            if (dto.getRouteDirOd() != null) {
                builder.setRouteDirOd(RouteDirOd.valueOf(dto.getRouteDirOd().name()));
            }

            // StopId 중첩 메시지 변환
            if (dto.getLastStopId() != null) {
                builder.setLastStopId(StopId.newBuilder().setValue(dto.getLastStopId().getValue()).build());
            }

            // UnsignedChar 중첩 메시지 변환
            if (dto.getLastStopSeq() != null) {
                builder.setLastStopSeq(UnsignedChar.newBuilder().setValue(dto.getLastStopSeq().getValue()).build());
            }

            Location message = builder.build();

            String topic = "obe/tbus/inb/187000001/187000010/6200/190000111/10";

            publisher.send(topic, message.toByteArray());

            log.info("Published BMS data protobuf message - topic: {}, Size: {} bytes",
                    topic, message.getSerializedSize());

        } catch (Exception e) {
            log.error("Failed to publish BMS data protobuf message: {}", e.getMessage(), e);
        }
    }


}
