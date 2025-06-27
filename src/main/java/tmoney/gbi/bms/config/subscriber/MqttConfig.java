package tmoney.gbi.bms.config.subscriber;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
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
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MqttConfig {

    private final MqttProperties mqttProperties;

    @Bean
    @Qualifier("subscriberClientManager")
    public Mqttv5ClientManager subscriberClientManager() {
        log.info("Creating MQTT subscriber client manager with URL: {}", mqttProperties.getBroker().getUrl());

        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setServerURIs(new String[]{mqttProperties.getBroker().getUrl()});
        options.setUserName(mqttProperties.getBroker().getUsername());
        options.setPassword(mqttProperties.getBroker().getPassword().getBytes());
        options.setCleanStart(true);
        options.setConnectionTimeout(30);
        options.setKeepAliveInterval(60);
        options.setAutomaticReconnect(true);

        // 추가 연결 옵션
        options.setMaxReconnectDelay(5000);
        options.setReceiveMaximum(100);

        String clientId = mqttProperties.getClient().getId() + "-subscriber";
        log.info("MQTT subscriber client ID: {}", clientId);

        Mqttv5ClientManager clientManager = new Mqttv5ClientManager(options, clientId);

        return clientManager;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        DirectChannel channel = new DirectChannel();
        log.info("Created MQTT input channel: {}", channel);
        return channel;
    }

    @Bean
    public MessageProducer mqttInbound(@Qualifier("subscriberClientManager") Mqttv5ClientManager clientManager) {
        log.info("Creating MQTT inbound adapter with topics: {}", mqttProperties.getTopics());

        Mqttv5PahoMessageDrivenChannelAdapter adapter = new Mqttv5PahoMessageDrivenChannelAdapter(
                clientManager,
                mqttProperties.getTopics().toArray(new String[0])
        );

        adapter.setCompletionTimeout(10000); // 10초로 증가
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(mqttProperties.getBroker().getQos());
        adapter.setOutputChannel(mqttInputChannel());

        // 에러 채널 설정
        adapter.setErrorChannelName("errorChannel");

        // 자동 시작 설정
        adapter.setAutoStartup(true);

        log.info("MQTT inbound adapter configured - QoS: {}, Topics: {}",
                mqttProperties.getBroker().getQos(), mqttProperties.getTopics());

        return adapter;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application ready - MQTT configuration completed");
        log.info("MQTT Broker URL: {}", mqttProperties.getBroker().getUrl());
        log.info("MQTT Username: {}", mqttProperties.getBroker().getUsername());
        log.info("MQTT Topics: {}", mqttProperties.getTopics());
        log.info("MQTT QoS: {}", mqttProperties.getBroker().getQos());
    }
}