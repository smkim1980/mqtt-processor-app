package tmoney.gbi.bms.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "mqtt")
public class MqttProperties {

    /**
     * MQTT 브로커 접속 정보
     */
    private Broker broker;

    /**
     * MQTT 클라이언트 정보
     */
    private Client client;

    /**
     * 구독할 토픽 목록
     */
    private List<String> topics;

    // Getters and Setters

    public Broker getBroker() {
        return broker;
    }

    public void setBroker(Broker broker) {
        this.broker = broker;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }


    /**
     * mqtt.broker 하위 프로퍼티를 위한 정적 중첩 클래스
     */
    public static class Broker {
        private String url;
        private String username;
        private String password;
        private int qos;

        // Getters and Setters

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getQos() {
            return qos;
        }

        public void setQos(int qos) {
            this.qos = qos;
        }
    }

    /**
     * mqtt.client 하위 프로퍼티를 위한 정적 중첩 클래스
     */
    public static class Client {
        private String id;

        // Getters and Setters

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
