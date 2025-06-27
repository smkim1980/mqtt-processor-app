package tmoney.gbi.bms.config.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/mqtt/test")
@RequiredArgsConstructor
public class MqttTestController {

    private final MessageChannel mqttOutboundChannel;

    @PostMapping("/publish/text")
    public ResponseEntity<String> publishTextMessage(
            @RequestParam String topic,
            @RequestParam String message) {

        try {
            log.info("Publishing text message to topic: {}, message: {}", topic, message);

            mqttOutboundChannel.send(
                    MessageBuilder.withPayload(message)
                            .setHeader("mqtt_topic", topic)
                            .setHeader("mqtt_qos", 1)
                            .setHeader("mqtt_retained", false)
                            .build()
            );

            return ResponseEntity.ok("Text message published successfully");
        } catch (Exception e) {
            log.error("Failed to publish text message: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Failed to publish text message: " + e.getMessage());
        }
    }

    @PostMapping("/publish/simple")
    public ResponseEntity<String> publishSimpleMessage() {
        try {
            String testMessage = "Hello MQTT from BMS System!";
            String testTopic = "test/topic";

            log.info("Publishing simple test message to topic: {}", testTopic);

            mqttOutboundChannel.send(
                    MessageBuilder.withPayload(testMessage)
                            .setHeader("mqtt_topic", testTopic)
                            .setHeader("mqtt_qos", 1)
                            .setHeader("mqtt_retained", false)
                            .build()
            );

            return ResponseEntity.ok("Simple test message published to " + testTopic);
        } catch (Exception e) {
            log.error("Failed to publish simple message: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Failed to publish simple message: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("MQTT Test Controller is running");
    }
}
