package tmoney.gbi.bms.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BmsData {
    private String deviceId;
    private String terminalId;
    private Double voltage;
    private Double current;
    private Double temperature;
    private Integer batteryLevel;
    private String status;
    private LocalDateTime timestamp;
}
