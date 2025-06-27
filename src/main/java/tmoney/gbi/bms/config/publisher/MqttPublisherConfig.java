package tmoney.gbi.bms.config.publisher;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.outbound.Mqttv5PahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import tmoney.gbi.bms.config.properties.MqttProperties;

/**
 * MQTT Publisher(발행자) 설정
 */
@Configuration
@Profile("local")
@RequiredArgsConstructor
public class MqttPublisherConfig {

    private final MqttProperties mqttProperties;
    private static final String PUBLISHER_CLIENT_ID = "bms-center-publisher";

    /**
     * MQTT 메시지를 발행하는 아웃바운드 핸들러.
     * ClientManager Bean을 주입받는 대신, 이 메소드 내에서 직접 연결 옵션을 생성합니다.
     * 이를 통해 의존성 주입 문제를 완전히 우회하고, 핸들러가 독립적으로 동작하도록 합니다.
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        // 1. 발행자 전용 연결 옵션을 직접 생성합니다.
        MqttConnectionOptions connectionOptions = new MqttConnectionOptions();
        connectionOptions.setServerURIs(new String[]{mqttProperties.getBroker().getUrl()});
        connectionOptions.setUserName(mqttProperties.getBroker().getUsername());
        connectionOptions.setPassword(mqttProperties.getBroker().getPassword().getBytes());
        connectionOptions.setCleanStart(true);
        connectionOptions.setConnectionTimeout(30);
        connectionOptions.setKeepAliveInterval(60);
        connectionOptions.setAutomaticReconnect(true);

        // 2. 연결 옵션과 고유 클라이언트 ID를 사용하는 생성자로 MessageHandler를 생성합니다.
        // 이제 이 핸들러는 자신만의 MQTT 클라이언트를 생성하고 관리합니다.
        Mqttv5PahoMessageHandler messageHandler =
                new Mqttv5PahoMessageHandler(connectionOptions, PUBLISHER_CLIENT_ID);

        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("bms/default");
        messageHandler.setDefaultQos(1);
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MyGateway {
        void sendToMqtt(String data);
    }
}
