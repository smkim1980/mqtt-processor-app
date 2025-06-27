package tmoney.gbi.bms.common.constant;

import static tmoney.gbi.bms.common.constant.TopicRuleNames.InfoType.*;
import static tmoney.gbi.bms.common.constant.TopicRuleNames.InbOutB.*;
import static tmoney.gbi.bms.common.constant.TopicRuleNames.RoutePoint.*;

/**
 * MQTT 구독을 위한 최종 토픽 문자열을 상수로 정의하는 클래스
 * TopicRuleNames의 모든 상수를 조합하여 생성되었습니다.
 */
public final class MqttTopicConstants {

    private MqttTopicConstants() {
        // 인스턴스화 방지
    }

    private static final String SLASH = "/";
    private static final String ALL_SUB_LEVELS = "/#";

    // --- OBE (obe) 정보 타입 토픽 ---
    public static final String OBE_TBUS_INB_TOPIC = OBE + SLASH + TBUS + SLASH + INB + ALL_SUB_LEVELS;
    public static final String OBE_TBUS_OUTB_TOPIC = OBE + SLASH + TBUS + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String OBE_BBUB_INB_TOPIC = OBE + SLASH + BBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String OBE_BBUB_OUTB_TOPIC = OBE + SLASH + BBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String OBE_MBUB_INB_TOPIC = OBE + SLASH + MBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String OBE_MBUB_OUTB_TOPIC = OBE + SLASH + MBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String OBE_OBUB_INB_TOPIC = OBE + SLASH + OBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String OBE_OBUB_OUTB_TOPIC = OBE + SLASH + OBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String OBE_TOBUS_INB_TOPIC = OBE + SLASH + TOBUS + SLASH + INB + ALL_SUB_LEVELS;
    public static final String OBE_TOBUS_OUTB_TOPIC = OBE + SLASH + TOBUS + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String OBE_ABUB_INB_TOPIC = OBE + SLASH + ABUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String OBE_ABUB_OUTB_TOPIC = OBE + SLASH + ABUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String OBE_APBUB_INB_TOPIC = OBE + SLASH + APBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String OBE_APBUB_OUTB_TOPIC = OBE + SLASH + APBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String OBE_NBUB_INB_TOPIC = OBE + SLASH + NBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String OBE_NBUB_OUTB_TOPIC = OBE + SLASH + NBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String OBE_GYEONGGI_INB_TOPIC = OBE + SLASH + GYEONGGI + SLASH + INB + ALL_SUB_LEVELS;
    public static final String OBE_GYEONGGI_OUTB_TOPIC = OBE + SLASH + GYEONGGI + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String OBE_INOHEON_INB_TOPIC = OBE + SLASH + INOHEON + SLASH + INB + ALL_SUB_LEVELS;
    public static final String OBE_INOHEON_OUTB_TOPIC = OBE + SLASH + INOHEON + SLASH + OUTB + ALL_SUB_LEVELS;

    // --- BIT (bit) 정보 타입 토픽 ---
    public static final String BIT_TBUS_INB_TOPIC = BIT + SLASH + TBUS + SLASH + INB + ALL_SUB_LEVELS;
    public static final String BIT_TBUS_OUTB_TOPIC = BIT + SLASH + TBUS + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String BIT_BBUB_INB_TOPIC = BIT + SLASH + BBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String BIT_BBUB_OUTB_TOPIC = BIT + SLASH + BBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String BIT_MBUB_INB_TOPIC = BIT + SLASH + MBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String BIT_MBUB_OUTB_TOPIC = BIT + SLASH + MBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String BIT_OBUB_INB_TOPIC = BIT + SLASH + OBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String BIT_OBUB_OUTB_TOPIC = BIT + SLASH + OBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String BIT_TOBUS_INB_TOPIC = BIT + SLASH + TOBUS + SLASH + INB + ALL_SUB_LEVELS;
    public static final String BIT_TOBUS_OUTB_TOPIC = BIT + SLASH + TOBUS + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String BIT_ABUB_INB_TOPIC = BIT + SLASH + ABUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String BIT_ABUB_OUTB_TOPIC = BIT + SLASH + ABUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String BIT_APBUB_INB_TOPIC = BIT + SLASH + APBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String BIT_APBUB_OUTB_TOPIC = BIT + SLASH + APBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String BIT_NBUB_INB_TOPIC = BIT + SLASH + NBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String BIT_NBUB_OUTB_TOPIC = BIT + SLASH + NBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String BIT_GYEONGGI_INB_TOPIC = BIT + SLASH + GYEONGGI + SLASH + INB + ALL_SUB_LEVELS;
    public static final String BIT_GYEONGGI_OUTB_TOPIC = BIT + SLASH + GYEONGGI + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String BIT_INOHEON_INB_TOPIC = BIT + SLASH + INOHEON + SLASH + INB + ALL_SUB_LEVELS;
    public static final String BIT_INOHEON_OUTB_TOPIC = BIT + SLASH + INOHEON + SLASH + OUTB + ALL_SUB_LEVELS;

    // --- SUB (sub) 정보 타입 토픽 ---
    public static final String SUB_TBUS_INB_TOPIC = SUB + SLASH + TBUS + SLASH + INB + ALL_SUB_LEVELS;
    public static final String SUB_TBUS_OUTB_TOPIC = SUB + SLASH + TBUS + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String SUB_BBUB_INB_TOPIC = SUB + SLASH + BBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String SUB_BBUB_OUTB_TOPIC = SUB + SLASH + BBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String SUB_MBUB_INB_TOPIC = SUB + SLASH + MBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String SUB_MBUB_OUTB_TOPIC = SUB + SLASH + MBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String SUB_OBUB_INB_TOPIC = SUB + SLASH + OBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String SUB_OBUB_OUTB_TOPIC = SUB + SLASH + OBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String SUB_TOBUS_INB_TOPIC = SUB + SLASH + TOBUS + SLASH + INB + ALL_SUB_LEVELS;
    public static final String SUB_TOBUS_OUTB_TOPIC = SUB + SLASH + TOBUS + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String SUB_ABUB_INB_TOPIC = SUB + SLASH + ABUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String SUB_ABUB_OUTB_TOPIC = SUB + SLASH + ABUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String SUB_APBUB_INB_TOPIC = SUB + SLASH + APBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String SUB_APBUB_OUTB_TOPIC = SUB + SLASH + APBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String SUB_NBUB_INB_TOPIC = SUB + SLASH + NBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String SUB_NBUB_OUTB_TOPIC = SUB + SLASH + NBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String SUB_GYEONGGI_INB_TOPIC = SUB + SLASH + GYEONGGI + SLASH + INB + ALL_SUB_LEVELS;
    public static final String SUB_GYEONGGI_OUTB_TOPIC = SUB + SLASH + GYEONGGI + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String SUB_INOHEON_INB_TOPIC = SUB + SLASH + INOHEON + SLASH + INB + ALL_SUB_LEVELS;
    public static final String SUB_INOHEON_OUTB_TOPIC = SUB + SLASH + INOHEON + SLASH + OUTB + ALL_SUB_LEVELS;

    // --- DBB (dbB) 정보 타입 토픽 ---
    public static final String DBB_TBUS_INB_TOPIC = DBB + SLASH + TBUS + SLASH + INB + ALL_SUB_LEVELS;
    public static final String DBB_TBUS_OUTB_TOPIC = DBB + SLASH + TBUS + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String DBB_BBUB_INB_TOPIC = DBB + SLASH + BBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String DBB_BBUB_OUTB_TOPIC = DBB + SLASH + BBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String DBB_MBUB_INB_TOPIC = DBB + SLASH + MBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String DBB_MBUB_OUTB_TOPIC = DBB + SLASH + MBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String DBB_OBUB_INB_TOPIC = DBB + SLASH + OBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String DBB_OBUB_OUTB_TOPIC = DBB + SLASH + OBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String DBB_TOBUS_INB_TOPIC = DBB + SLASH + TOBUS + SLASH + INB + ALL_SUB_LEVELS;
    public static final String DBB_TOBUS_OUTB_TOPIC = DBB + SLASH + TOBUS + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String DBB_ABUB_INB_TOPIC = DBB + SLASH + ABUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String DBB_ABUB_OUTB_TOPIC = DBB + SLASH + ABUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String DBB_APBUB_INB_TOPIC = DBB + SLASH + APBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String DBB_APBUB_OUTB_TOPIC = DBB + SLASH + APBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String DBB_NBUB_INB_TOPIC = DBB + SLASH + NBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String DBB_NBUB_OUTB_TOPIC = DBB + SLASH + NBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String DBB_GYEONGGI_INB_TOPIC = DBB + SLASH + GYEONGGI + SLASH + INB + ALL_SUB_LEVELS;
    public static final String DBB_GYEONGGI_OUTB_TOPIC = DBB + SLASH + GYEONGGI + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String DBB_INOHEON_INB_TOPIC = DBB + SLASH + INOHEON + SLASH + INB + ALL_SUB_LEVELS;
    public static final String DBB_INOHEON_OUTB_TOPIC = DBB + SLASH + INOHEON + SLASH + OUTB + ALL_SUB_LEVELS;

    // --- API (api) 정보 타입 토픽 ---
    public static final String API_TBUS_INB_TOPIC = API + SLASH + TBUS + SLASH + INB + ALL_SUB_LEVELS;
    public static final String API_TBUS_OUTB_TOPIC = API + SLASH + TBUS + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String API_BBUB_INB_TOPIC = API + SLASH + BBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String API_BBUB_OUTB_TOPIC = API + SLASH + BBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String API_MBUB_INB_TOPIC = API + SLASH + MBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String API_MBUB_OUTB_TOPIC = API + SLASH + MBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String API_OBUB_INB_TOPIC = API + SLASH + OBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String API_OBUB_OUTB_TOPIC = API + SLASH + OBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String API_TOBUS_INB_TOPIC = API + SLASH + TOBUS + SLASH + INB + ALL_SUB_LEVELS;
    public static final String API_TOBUS_OUTB_TOPIC = API + SLASH + TOBUS + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String API_ABUB_INB_TOPIC = API + SLASH + ABUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String API_ABUB_OUTB_TOPIC = API + SLASH + ABUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String API_APBUB_INB_TOPIC = API + SLASH + APBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String API_APBUB_OUTB_TOPIC = API + SLASH + APBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String API_NBUB_INB_TOPIC = API + SLASH + NBUB + SLASH + INB + ALL_SUB_LEVELS;
    public static final String API_NBUB_OUTB_TOPIC = API + SLASH + NBUB + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String API_GYEONGGI_INB_TOPIC = API + SLASH + GYEONGGI + SLASH + INB + ALL_SUB_LEVELS;
    public static final String API_GYEONGGI_OUTB_TOPIC = API + SLASH + GYEONGGI + SLASH + OUTB + ALL_SUB_LEVELS;
    public static final String API_INOHEON_INB_TOPIC = API + SLASH + INOHEON + SLASH + INB + ALL_SUB_LEVELS;
    public static final String API_INOHEON_OUTB_TOPIC = API + SLASH + INOHEON + SLASH + OUTB + ALL_SUB_LEVELS;
}