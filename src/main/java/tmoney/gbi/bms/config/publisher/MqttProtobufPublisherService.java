package tmoney.gbi.bms.config.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;
import tmoney.gbi.bms.model.BmsData;
import tmoney.gbi.bms.proto.*;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqttProtobufPublisherService {

    private final MessageChannel mqttOutboundChannel;

    /**
     * BMS 데이터를 Protobuf 메시지로 발송
     */
    public void publishBmsData(BmsData bmsData) {
        try {
            BmsDataMessage.Builder builder = BmsDataMessage.newBuilder()
                    .setDeviceId(bmsData.getDeviceId())
                    .setTerminalId(bmsData.getTerminalId() != null ? bmsData.getTerminalId() : "")
                    .setVoltage(bmsData.getVoltage() != null ? bmsData.getVoltage() : 0.0)
                    .setCurrent(bmsData.getCurrent() != null ? bmsData.getCurrent() : 0.0)
                    .setTemperature(bmsData.getTemperature() != null ? bmsData.getTemperature() : 0.0)
                    .setBatteryLevel(bmsData.getBatteryLevel() != null ? bmsData.getBatteryLevel() : 0)
                    .setStatus(convertToBmsStatus(bmsData.getStatus()))
                    .setTimestamp(bmsData.getTimestamp() != null ?
                            bmsData.getTimestamp().toInstant(ZoneOffset.UTC).toEpochMilli() :
                            System.currentTimeMillis());

            // 추가 센서 데이터 예시
            builder.addSensorReadings(
                    SensorReading.newBuilder()
                            .setSensorType("VOLTAGE")
                            .setValue(bmsData.getVoltage() != null ? bmsData.getVoltage() : 0.0)
                            .setUnit("V")
                            .setReadingTime(System.currentTimeMillis())
                            .build()
            );

            BmsDataMessage message = builder.build();
            String topic = "bms/data/" + bmsData.getDeviceId();

            publishProtobufMessage(topic, message.toByteArray());
            log.info("Published BMS data protobuf message - Device: {}, Size: {} bytes",
                    bmsData.getDeviceId(), message.getSerializedSize());

        } catch (Exception e) {
            log.error("Failed to publish BMS data protobuf message: {}", e.getMessage(), e);
        }
    }

    /**
     * BMS 상태를 Protobuf 메시지로 발송
     */
    public void publishBmsStatus(String deviceId, BmsStatus status, String message) {
        publishBmsStatus(deviceId, status, message, null);
    }

    public void publishBmsStatus(String deviceId, BmsStatus status, String message, StatusDetails details) {
        try {
            BmsStatusMessage.Builder builder = BmsStatusMessage.newBuilder()
                    .setDeviceId(deviceId)
                    .setStatus(status)
                    .setMessage(message != null ? message : "")
                    .setTimestamp(System.currentTimeMillis());

            if (details != null) {
                builder.setDetails(details);
            }

            BmsStatusMessage statusMessage = builder.build();
            String topic = "bms/status/" + deviceId;

            publishProtobufMessage(topic, statusMessage.toByteArray());
            log.info("Published BMS status protobuf message - Device: {}, Status: {}", deviceId, status);

        } catch (Exception e) {
            log.error("Failed to publish BMS status protobuf message: {}", e.getMessage(), e);
        }
    }

    /**
     * BMS 명령을 Protobuf 메시지로 발송
     */
    public void publishBmsCommand(String deviceId, CommandType commandType, String payload) {
        publishBmsCommand(deviceId, commandType, payload, null, null);
    }

    public void publishBmsCommand(String deviceId, CommandType commandType, String payload,
                                  String requestId, Map<String, String> parameters) {
        try {
            BmsCommandMessage.Builder builder = BmsCommandMessage.newBuilder()
                    .setDeviceId(deviceId)
                    .setCommandType(commandType)
                    .setCommandPayload(payload != null ? payload : "")
                    .setRequestId(requestId != null ? requestId : generateRequestId())
                    .setTimestamp(System.currentTimeMillis());

            if (parameters != null) {
                builder.putAllParameters(parameters);
            }

            BmsCommandMessage commandMessage = builder.build();
            String topic = "bms/command/" + deviceId;

            publishProtobufMessage(topic, commandMessage.toByteArray());
            log.info("Published BMS command protobuf message - Device: {}, Command: {}", deviceId, commandType);

        } catch (Exception e) {
            log.error("Failed to publish BMS command protobuf message: {}", e.getMessage(), e);
        }
    }

    /**
     * BMS 응답을 Protobuf 메시지로 발송
     */
    public void publishBmsResponse(String deviceId, String requestId, ResponseStatus status,
                                   String responseMessage, String responseData) {
        try {
            BmsResponseMessage.Builder builder = BmsResponseMessage.newBuilder()
                    .setDeviceId(deviceId)
                    .setRequestId(requestId)
                    .setResponseStatus(status)
                    .setResponseMessage(responseMessage != null ? responseMessage : "")
                    .setTimestamp(System.currentTimeMillis());

            if (responseData != null) {
                builder.setResponseData(responseData);
            }

            BmsResponseMessage response = builder.build();
            String topic = "bms/response/" + deviceId;

            publishProtobufMessage(topic, response.toByteArray());
            log.info("Published BMS response protobuf message - Device: {}, RequestId: {}, Status: {}",
                    deviceId, requestId, status);

        } catch (Exception e) {
            log.error("Failed to publish BMS response protobuf message: {}", e.getMessage(), e);
        }
    }

    /**
     * Protobuf 바이트 배열을 MQTT로 발송
     */
    private void publishProtobufMessage(String topic, byte[] payload) {
        mqttOutboundChannel.send(
                MessageBuilder.withPayload(payload)
                        .setHeader("mqtt_topic", topic)
                        .setHeader("mqtt_qos", 1)
                        .setHeader("mqtt_retained", false)
                        .setHeader("content_type", "application/x-protobuf")
                        .build()
        );
    }

    /**
     * 문자열 상태를 BmsStatus enum으로 변환
     */
    private BmsStatus convertToBmsStatus(String status) {
        if (status == null) return BmsStatus.UNKNOWN;

        try {
            return BmsStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown BMS status: {}, using UNKNOWN", status);
            return BmsStatus.UNKNOWN;
        }
    }

    /**
     * 요청 ID 생성
     */
    private String generateRequestId() {
        return "REQ_" + System.currentTimeMillis() + "_" +
                Integer.toHexString((int)(Math.random() * 0xFFFF));
    }
}

