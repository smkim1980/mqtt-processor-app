package tmoney.gbi.bms.sample;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tmoney.gbi.bms.domain.LocationDto;

@RestController
@RequestMapping("/api/mqtt/protobuf")
@RequiredArgsConstructor
public class MqttProtobufTestController {
    private final MqttProtobufPublisherService protobufPublisherService;

    @PostMapping("/publish/data")
    public ResponseEntity<String> publishBmsData(@RequestBody LocationDto locationDto) {

        try {
            protobufPublisherService.publishLocationData(locationDto);

            return ResponseEntity.ok("BMS data published successfully as Protobuf");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to publish BMS data: " + e.getMessage());
        }
    }
}
