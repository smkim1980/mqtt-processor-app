package tmoney.gbi.bms.handler;

import com.github.tocrhz.mqtt.annotation.MqttSubscribe;
import com.github.tocrhz.mqtt.annotation.Payload;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;
import tmoney.gbi.bms.proto.Location;

import static tmoney.gbi.bms.common.constant.MqttTopicConstants.OBE_TBUS_INB_TOPIC;
import static tmoney.gbi.bms.common.constant.TopicRuleNames.QOS_1;

@Component
@Slf4j
public class MqttMessageHandler {
    /**
     * "bms/topic1"은 QoS 0으로,
     * "bms/topic2"와 "bms/topic3"은 QoS 1로 구독됩니다.
     */
    @MqttSubscribe(value = OBE_TBUS_INB_TOPIC, qos = QOS_1)
    public void subscribeMixedQos(String topic, MqttMessage message, @Payload() byte[] payload) throws Exception {
        log.info("######################### MqttMessage PayLoad locationData :{}", Location.parseFrom(message.getPayload()));
        handlerLocationsData(topic, payload);
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