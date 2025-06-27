package tmoney.gbi.bms.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import tmoney.gbi.bms.proto.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqttProtobufMessageHandler {


    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleProtobufMessage(Message<?> message) {
        try {
            MessageHeaders headers = message.getHeaders();
            String topic = (String) headers.get("mqtt_receivedTopic");
            Object payload = message.getPayload();
            Integer qos = (Integer) headers.get("mqtt_receivedQos");
            Boolean retained = (Boolean) headers.get("mqtt_receivedRetained");

            log.info("=== MQTT Message Received ===");
            log.info("Topic: {}", topic);
            log.info("QoS: {}", qos);
            log.info("Retained: {}", retained);
            log.info("Payload Type: {}", payload != null ? payload.getClass().getSimpleName() : "null");
            log.info("Headers: {}", headers);

            if (payload instanceof byte[]) {
                byte[] payloadBytes = (byte[]) payload;
                log.info("Payload Size: {} bytes", payloadBytes.length);
                log.info("Payload (hex): {}", bytesToHex(payloadBytes));

                if (topic != null) {
                    handleProtobufPayload(topic, payloadBytes);
                } else {
                    log.warn("Topic is null, cannot process message");
                }
            } else if (payload instanceof String) {
                String textPayload = (String) payload;
                log.info("Text Payload: {}", textPayload);
                handleTextMessage(topic, textPayload);
            } else {
                log.warn("Unknown payload type: {}", payload != null ? payload.getClass() : "null");
            }

            log.info("=== Message Processing Complete ===");

        } catch (Exception e) {
            log.error("Error processing MQTT message: {}", e.getMessage(), e);
        }
    }

    private void handleTextMessage(String topic, String payload) {
        log.info("Processing text message - Topic: {}, Payload: {}", topic, payload);

        // 간단한 텍스트 메시지 처리 로직
        if ("test/topic".equals(topic)) {
            log.info("Test message received: {}", payload);
        }
    }

    private void handleProtobufPayload(String topic, byte[] payload) {
        try {
            log.info("Processing protobuf payload for topic: {}", topic);

            if (topic.startsWith("bms/data/")) {
                handleBmsDataMessage(topic, payload);
            } else if (topic.startsWith("bms/status/")) {
                handleBmsStatusMessage(topic, payload);
            } else if (topic.startsWith("bms/command/")) {
                handleBmsCommandMessage(topic, payload);
            } else if (topic.startsWith("bms/response/")) {
                handleBmsResponseMessage(topic, payload);
            } else {
                log.warn("Unknown topic pattern: {}, trying to parse as text", topic);
                try {
                    String textPayload = new String(payload);
                    log.info("Parsed as text: {}", textPayload);
                } catch (Exception e) {
                    log.warn("Could not parse as text either: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to process protobuf message from topic {}: {}", topic, e.getMessage(), e);

            // 실패한 경우 원시 데이터 로깅
            log.info("Raw payload bytes: {}", bytesToHex(payload));

            // 텍스트로 파싱 시도
            try {
                String textPayload = new String(payload);
                log.info("Attempting to parse as text: {}", textPayload);
            } catch (Exception textParseException) {
                log.warn("Could not parse as text: {}", textParseException.getMessage());
            }
        }
    }

    private void handleBmsDataMessage(String topic, byte[] payload) throws InvalidProtocolBufferException {
        try {
            BmsDataMessage bmsData = BmsDataMessage.parseFrom(payload);
            log.info(">>> BMS Data Received <<<");
            log.info("Device: {}", bmsData.getDeviceId());
            log.info("Terminal: {}", bmsData.getTerminalId());
            log.info("Voltage: {}V", bmsData.getVoltage());
            log.info("Current: {}A", bmsData.getCurrent());
            log.info("Temperature: {}°C", bmsData.getTemperature());
            log.info("Battery: {}%", bmsData.getBatteryLevel());
            log.info("Status: {}", bmsData.getStatus());

            processBmsData(bmsData);
        } catch (InvalidProtocolBufferException e) {
            log.error("Failed to parse BMS data message: {}", e.getMessage());
            throw e;
        }
    }

    private void handleBmsStatusMessage(String topic, byte[] payload) throws InvalidProtocolBufferException {
        try {
            BmsStatusMessage statusMessage = BmsStatusMessage.parseFrom(payload);
            log.info(">>> BMS Status Received <<<");
            log.info("Device: {}", statusMessage.getDeviceId());
            log.info("Status: {}", statusMessage.getStatus());
            log.info("Message: {}", statusMessage.getMessage());

            processBmsStatus(statusMessage);
        } catch (InvalidProtocolBufferException e) {
            log.error("Failed to parse BMS status message: {}", e.getMessage());
            throw e;
        }
    }

    private void handleBmsCommandMessage(String topic, byte[] payload) throws InvalidProtocolBufferException {
        try {
            BmsCommandMessage commandMessage = BmsCommandMessage.parseFrom(payload);
            log.info(">>> BMS Command Received <<<");
            log.info("Device: {}", commandMessage.getDeviceId());
            log.info("Command: {}", commandMessage.getCommandType());
            log.info("Request ID: {}", commandMessage.getRequestId());
            log.info("Payload: {}", commandMessage.getCommandPayload());

            ResponseStatus responseStatus = processBmsCommand(commandMessage);
        } catch (InvalidProtocolBufferException e) {
            log.error("Failed to parse BMS command message: {}", e.getMessage());
            throw e;
        }
    }

    private void handleBmsResponseMessage(String topic, byte[] payload) throws InvalidProtocolBufferException {
        try {
            BmsResponseMessage responseMessage = BmsResponseMessage.parseFrom(payload);
            log.info(">>> BMS Response Received <<<");
            log.info("Device: {}", responseMessage.getDeviceId());
            log.info("Request ID: {}", responseMessage.getRequestId());
            log.info("Status: {}", responseMessage.getResponseStatus());
            log.info("Message: {}", responseMessage.getResponseMessage());

            processBmsResponse(responseMessage);
        } catch (InvalidProtocolBufferException e) {
            log.error("Failed to parse BMS response message: {}", e.getMessage());
            throw e;
        }
    }

    private void processBmsData(BmsDataMessage bmsData) {
        // 배터리 레벨이 낮으면 경고 상태 발송
        if (bmsData.getBatteryLevel() < 20) {
            log.warn("Low battery detected for device: {}, level: {}%",
                    bmsData.getDeviceId(), bmsData.getBatteryLevel());
        }

        // 온도가 높으면 경고
        if (bmsData.getTemperature() > 40.0) {
            log.warn("High temperature detected for device: {}, temperature: {}°C",
                    bmsData.getDeviceId(), bmsData.getTemperature());
        }
    }

    private void processBmsStatus(BmsStatusMessage statusMessage) {
        log.info("Processing BMS status for device: {}", statusMessage.getDeviceId());
        // 상태 처리 로직 구현
    }

    private ResponseStatus processBmsCommand(BmsCommandMessage commandMessage) {
        try {
            log.info("Processing command {} for device: {}",
                    commandMessage.getCommandType(), commandMessage.getDeviceId());

            switch (commandMessage.getCommandType()) {
                case RESTART:
                    log.info("Executing RESTART command for device: {}", commandMessage.getDeviceId());
                    return ResponseStatus.SUCCESS;
                case SHUTDOWN:
                    log.info("Executing SHUTDOWN command for device: {}", commandMessage.getDeviceId());
                    return ResponseStatus.SUCCESS;
                case RESET:
                    log.info("Executing RESET command for device: {}", commandMessage.getDeviceId());
                    return ResponseStatus.SUCCESS;
                case UPDATE_CONFIG:
                    log.info("Executing UPDATE_CONFIG command for device: {}", commandMessage.getDeviceId());
                    return ResponseStatus.SUCCESS;
                case DIAGNOSTIC:
                    log.info("Executing DIAGNOSTIC command for device: {}", commandMessage.getDeviceId());
                    return ResponseStatus.SUCCESS;
                default:
                    log.warn("Unknown command type: {} for device: {}",
                            commandMessage.getCommandType(), commandMessage.getDeviceId());
                    return ResponseStatus.FAILED;
            }
        } catch (Exception e) {
            log.error("Failed to process command for device {}: {}",
                    commandMessage.getDeviceId(), e.getMessage(), e);
            return ResponseStatus.FAILED;
        }
    }

    private void processBmsResponse(BmsResponseMessage responseMessage) {
        log.info("Processing BMS response for device: {}, request: {}",
                responseMessage.getDeviceId(), responseMessage.getRequestId());
        // 응답 처리 로직 구현
    }

    private String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(bytes.length, 50); i++) { // 최대 50바이트만 표시
            sb.append(String.format("%02x ", bytes[i]));
        }
        if (bytes.length > 50) {
            sb.append("...");
        }
        return sb.toString().trim();
    }
}