package tmoney.gbi.bms.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Alias("encryptedLocationDto")
public class EncryptedLocationDto {

    private String encryptedLatitude;
    private String encryptedLongitude;
    private float heading;
    private float altitude;
    private float speed;
    private String occurDt; // Unix timestamp in milliseconds
    private String routeDir;
    private String lastStopId;
    private long lastStopSeq;
    private long lastStopDist; // uint32는 long으로 처리하여 오버플로우 방지
    private long originAccumDist;

}
