package tmoney.gbi.bms.config.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tmoney.gbi.bms.model.BmsData;
import tmoney.gbi.bms.proto.BmsStatus;
import tmoney.gbi.bms.proto.CommandType;
import tmoney.gbi.bms.proto.ResponseStatus;
import tmoney.gbi.bms.proto.StatusDetails;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mqtt/protobuf")
@RequiredArgsConstructor
public class MqttProtobufTestController {

    private final MqttProtobufPublisherService protobufPublisherService;

    @PostMapping("/publish/data/{deviceId}")
    public ResponseEntity<String> publishBmsData(
            @PathVariable String deviceId,
            @RequestBody BmsData bmsData) {

        try {
            bmsData.setDeviceId(deviceId);
            bmsData.setTimestamp(LocalDateTime.now());

            protobufPublisherService.publishBmsData(bmsData);

            return ResponseEntity.ok("BMS data published successfully as Protobuf");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to publish BMS data: " + e.getMessage());
        }
    }

    @PostMapping("/publish/status/{deviceId}")
    public ResponseEntity<String> publishBmsStatus(
            @PathVariable String deviceId,
            @RequestParam String status,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) Integer errorCode) {

        try {
            BmsStatus bmsStatus = BmsStatus.valueOf(status.toUpperCase());

            // 상태 세부 정보 추가
            StatusDetails.Builder detailsBuilder = StatusDetails.newBuilder();
            if (errorCode != null) {
                detailsBuilder.setErrorCode(errorCode);
                detailsBuilder.setErrorMessage("Error code: " + errorCode);
            }
            detailsBuilder.setUptimeSeconds(System.currentTimeMillis() / 1000.0);
            detailsBuilder.setConnectionQuality(85); // 예시 값

            protobufPublisherService.publishBmsStatus(
                    deviceId,
                    bmsStatus,
                    message != null ? message : "Status update",
                    detailsBuilder.build()
            );

            return ResponseEntity.ok("BMS status published successfully as Protobuf");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to publish BMS status: " + e.getMessage());
        }
    }

    @PostMapping("/publish/command/{deviceId}")
    public ResponseEntity<String> publishBmsCommand(
            @PathVariable String deviceId,
            @RequestParam String command,
            @RequestParam(required = false) String payload,
            @RequestParam(required = false) String requestId) {

        try {
            CommandType commandType = CommandType.valueOf(command.toUpperCase());

            // 명령 매개변수 예시
            Map<String, String> parameters = new HashMap<>();
            parameters.put("priority", "HIGH");
            parameters.put("timeout", "30");

            protobufPublisherService.publishBmsCommand(
                    deviceId,
                    commandType,
                    payload != null ? payload : "",
                    requestId,
                    parameters
            );

            return ResponseEntity.ok("BMS command published successfully as Protobuf");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to publish BMS command: " + e.getMessage());
        }
    }

    @PostMapping("/publish/response/{deviceId}")
    public ResponseEntity<String> publishBmsResponse(
            @PathVariable String deviceId,
            @RequestParam String requestId,
            @RequestParam String status,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String data) {

        try {
            ResponseStatus responseStatus = ResponseStatus.valueOf(status.toUpperCase());

            protobufPublisherService.publishBmsResponse(
                    deviceId,
                    requestId,
                    responseStatus,
                    message != null ? message : "Response message",
                    data
            );

            return ResponseEntity.ok("BMS response published successfully as Protobuf");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to publish BMS response: " + e.getMessage());
        }
    }

    @GetMapping("/test/full-cycle/{deviceId}")
    public ResponseEntity<String> testFullCycle(@PathVariable String deviceId) {
        try {
            // 1. BMS 데이터 발송
            BmsData testData = BmsData.builder()
                    .deviceId(deviceId)
                    .terminalId("TEST_TERMINAL")
                    .voltage(12.5)
                    .current(7.2)
                    .temperature(25.0)
                    .batteryLevel(75)
                    .status("NORMAL")
                    .timestamp(LocalDateTime.now())
                    .build();

            protobufPublisherService.publishBmsData(testData);

            // 2. 상태 업데이트
            Thread.sleep(1000);
            protobufPublisherService.publishBmsStatus(
                    deviceId,
                    BmsStatus.NORMAL,
                    "System operational"
            );

            // 3. 명령 발송
            Thread.sleep(1000);
            protobufPublisherService.publishBmsCommand(
                    deviceId,
                    CommandType.DIAGNOSTIC,
                    "Run full diagnostic"
            );

            return ResponseEntity.ok("Full cycle test completed successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to execute full cycle test: " + e.getMessage());
        }
    }
}
