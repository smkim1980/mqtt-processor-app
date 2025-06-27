package tmoney.gbi.bms.handler;

import com.github.tocrhz.mqtt.annotation.MqttSubscribe;
import com.github.tocrhz.mqtt.annotation.Payload;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;
import tmoney.gbi.bms.proto.Location;

import java.nio.charset.StandardCharsets;

import static tmoney.gbi.bms.common.constant.MqttTopicConstants.OBE_TBUS_INB_TOPIC;

@Component
@Slf4j
public class MqttMessageHandler {
    /**
     * "bms/topic1"은 QoS 0으로,
     * "bms/topic2"와 "bms/topic3"은 QoS 1로 구독됩니다.
     */
    @MqttSubscribe(value = OBE_TBUS_INB_TOPIC, qos = 1)
    public void subscribeMixedQos(String topic, MqttMessage message, @Payload String payload) {
        log.info("receive from    : {}", topic);
        log.info("message payload : {}", new String(message.getPayload(), StandardCharsets.UTF_8));
        log.info("string payload  : {}", payload);

        try {
            handlerLocationsData(topic, (byte[]) message.getPayload());
        } catch (InvalidProtocolBufferException e) {
            log.error("Exception :: {}", ExceptionUtils.getStackTrace(e));
        }
    }

    private void handlerLocationsData(String topic, byte[] payload) throws InvalidProtocolBufferException, InvalidProtocolBufferException {
        Location locationData = Location.parseFrom(payload);
        log.info("######################### locationData :{}", locationData.toString());
    }

}