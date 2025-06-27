package tmoney.gbi.bms.model;

import lombok.Data;

import java.time.Instant;

/**
 * 이 파일은 Protobuf 메시지와 상호 작용하는 Java DTO (Data Transfer Objects) 및
 * 변환 로직의 예제를 보여줍니다.
 * Lombok 라이브러리가 필요합니다.
 */

// =======================================================
// DTO 클래스 정의 (Lombok 및 이너 클래스 적용)
// =======================================================

/**
 * Location Protobuf 메시지에 해당하는 메인 DTO 클래스.
 * 관련된 DTO들을 이너 클래스로 포함합니다.
 */
@Data
public class LocationDto {

    private double latitude;
    private double longitude;
    private float heading;
    private float altitude;
    private SpeedDto speed;
    private Instant occurAt;
    private RouteDirOdDto routeDirOd;
    private StopIdDto lastStopId;
    private UnsignedCharDto lastStopSeq;
    private long lastStopDist; // uint32는 long으로 처리하여 오버플로우 방지
    private long originAccumDist;

    /**
     * Speed 메시지에 해당하는 DTO 클래스
     */
    @Data
    public static class SpeedDto {
        private float value;
    }

    /**
     * StopId 메시지에 해당하는 DTO 클래스
     */
    @Data
    public static class StopIdDto {
        private String value;
    }

    /**
     * UnsignedChar 메시지에 해당하는 DTO 클래스
     */
    @Data
    public static class UnsignedCharDto {
        private int value; // Java에서는 unsigned char를 int로 표현하는 것이 일반적입니다.
    }

    /**
     * RouteDirOd Enum에 해당하는 Java Enum
     */
    public enum RouteDirOdDto {
        ROUTE_DIR_OD_UNKNOWN,
        UPLINE,
        DOWNLINE;
    }
}

