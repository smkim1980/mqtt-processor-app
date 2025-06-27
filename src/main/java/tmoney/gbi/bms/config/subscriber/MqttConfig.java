package tmoney.gbi.bms.config.subscriber;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.Mqttv5ClientManager;
import org.springframework.integration.mqtt.inbound.Mqttv5PahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import tmoney.gbi.bms.config.properties.MqttProperties;

/**
 * MQTT Subscriber(수신자) 설정
 */
@Configuration
@RequiredArgsConstructor
public class MqttConfig {

    private final MqttProperties mqttProperties;
    // MqttProtobufMessageHandler가 @Service로 독립적으로 동작하므로,
    // 이 설정 클래스에서는 더 이상 PublisherService에 대한 의존성이 필요 없습니다.
    // private final MqttProtobufPublisherService protobufPublisherService; <-- 이 줄을 삭제합니다.

    @Bean
    @Qualifier("subscriberClientManager")
    public Mqttv5ClientManager subscriberClientManager() {
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setServerURIs(new String[]{mqttProperties.getBroker().getUrl()});
        options.setUserName(mqttProperties.getBroker().getUsername());
        options.setPassword(mqttProperties.getBroker().getPassword().getBytes());
        options.setCleanStart(true);
        options.setConnectionTimeout(30);
        options.setKeepAliveInterval(60);
        options.setAutomaticReconnect(true);

        return new Mqttv5ClientManager(options, mqttProperties.getClient().getId());
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer mqttInbound(@Qualifier("subscriberClientManager") Mqttv5ClientManager clientManager) {
        Mqttv5PahoMessageDrivenChannelAdapter adapter = new Mqttv5PahoMessageDrivenChannelAdapter(
                clientManager,
                mqttProperties.getTopics().toArray(new String[0])
        );
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(mqttProperties.getBroker().getQos());
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }
}
