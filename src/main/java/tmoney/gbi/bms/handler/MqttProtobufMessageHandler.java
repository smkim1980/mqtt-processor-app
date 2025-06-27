package tmoney.gbi.bms.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import tmoney.gbi.bms.config.publisher.MqttProtobufPublisherService;
import tmoney.gbi.bms.proto.*;

@Slf4j
@Service // 1. 이 클래스가 스프링 Bean임을 선언합니다.
@RequiredArgsConstructor
public class MqttProtobufMessageHandler {

    private final MqttProtobufPublisherService protobufPublisherService;

    /**
     * 2. @ServiceActivator를 여기에 직접 추가하여
     * 이 메소드가 'mqttInputChannel'을 구독함을 명시적으로 선언합니다.
     */
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleProtobufMessage(Message<?> message) {
        String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
        Object payload = message.getPayload();

        log.info(">>>> Received MQTT message - Topic: {}, Payload type: {}", topic, payload.getClass().getSimpleName());

        if (topic != null && payload instanceof byte[]) {
            handleProtobufPayload(topic, (byte[]) payload);
        } else if (payload instanceof String) {
            log.info(">>>> Received text message - Topic: {}, Payload: {}", topic, payload);
        }
    }

    private void handleProtobufPayload(String topic, byte[] payload) {
        try {
            if (topic.startsWith("bms/data/")) {
                handleBmsDataMessage(topic, payload);
            } else if (topic.startsWith("bms/status/")) {
                handleBmsStatusMessage(topic, payload);
            } else if (topic.startsWith("bms/command/")) {
                handleBmsCommandMessage(topic, payload);
            } else if (topic.startsWith("bms/response/")) {
                handleBmsResponseMessage(topic, payload);
            } else {
                log.warn("Unknown topic pattern: {}", topic);
            }
        } catch (Exception e) {
            log.error("Failed to process protobuf message from topic {}: {}", topic, e.getMessage(), e);
        }
    }

    private void handleBmsDataMessage(String topic, byte[] payload) throws InvalidProtocolBufferException {
        BmsDataMessage bmsData = BmsDataMessage.parseFrom(payload);
        log.info(">>>> Received BMS Data - Device: {}, Voltage: {}V, Current: {}A, Temperature: {}°C, Battery: {}%, Status: {}",
                bmsData.getDeviceId(), bmsData.getVoltage(), bmsData.getCurrent(), bmsData.getTemperature(),
                bmsData.getBatteryLevel(), bmsData.getStatus());
        processBmsData(bmsData);
    }

    private void handleBmsStatusMessage(String topic, byte[] payload) throws InvalidProtocolBufferException {
        BmsStatusMessage statusMessage = BmsStatusMessage.parseFrom(payload);
        log.info(">>>> Received BMS Status - Device: {}, Status: {}, Message: {}",
                statusMessage.getDeviceId(), statusMessage.getStatus(), statusMessage.getMessage());
        processBmsStatus(statusMessage);
    }

    private void handleBmsCommandMessage(String topic, byte[] payload) throws InvalidProtocolBufferException {
        BmsCommandMessage commandMessage = BmsCommandMessage.parseFrom(payload);
        log.info(">>>> Received BMS Command - Device: {}, Command: {}, RequestId: {}",
                commandMessage.getDeviceId(), commandMessage.getCommandType(), commandMessage.getRequestId());
        ResponseStatus responseStatus = processBmsCommand(commandMessage);
        protobufPublisherService.publishBmsResponse(
                commandMessage.getDeviceId(), commandMessage.getRequestId(), responseStatus,
                "Command processed successfully", null);
    }

    private void handleBmsResponseMessage(String topic, byte[] payload) throws InvalidProtocolBufferException {
        BmsResponseMessage responseMessage = BmsResponseMessage.parseFrom(payload);
        log.info(">>>> Received BMS Response - Device: {}, RequestId: {}, Status: {}, Message: {}",
                responseMessage.getDeviceId(),
                responseMessage.getRequestId(),
                responseMessage.getResponseStatus(),
                responseMessage.getResponseMessage());
        processBmsResponse(responseMessage);
    }

    private void processBmsData(BmsDataMessage bmsData) {
        if (bmsData.getBatteryLevel() < 20) {
            protobufPublisherService.publishBmsStatus(bmsData.getDeviceId(), BmsStatus.WARNING,
                    "Low battery level: " + bmsData.getBatteryLevel() + "%");
        }
    }

    private void processBmsStatus(BmsStatusMessage statusMessage) {
    }

    private ResponseStatus processBmsCommand(BmsCommandMessage commandMessage) {
        try {
            switch (commandMessage.getCommandType()) {
                case RESTART:
                    log.info("Processing RESTART command for device: {}", commandMessage.getDeviceId());
                    return ResponseStatus.SUCCESS;
                case SHUTDOWN:
                    log.info("Processing SHUTDOWN command for device: {}", commandMessage.getDeviceId());
                    return ResponseStatus.SUCCESS;
                case RESET:
                    log.info("Processing RESET command for device: {}", commandMessage.getDeviceId());
                    return ResponseStatus.SUCCESS;
                case UPDATE_CONFIG:
                    log.info("Processing UPDATE_CONFIG command for device: {}", commandMessage.getDeviceId());
                    return ResponseStatus.SUCCESS;
                default:
                    log.warn("Unknown command type: {}", commandMessage.getCommandType());
                    return ResponseStatus.FAILED;
            }
        } catch (Exception e) {
            log.error("Failed to process command: {}", e.getMessage(), e);
            return ResponseStatus.FAILED;
        }
    }

    private void processBmsResponse(BmsResponseMessage responseMessage) {
    }
}
