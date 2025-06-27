package tmoney.gbi.bms.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TopicRuleNames {

    public static final int QOS_0 = 0;
    public static final int QOS_1 = 1;
    public static final int QOS_2= 2;


    public static class InfoType{
        public static final String OBE = "obe";
        public static final String BIT = "bit";
        public static final String SUB = "sub";
        public static final String DBB = "dbB";
        public static final String API = "api";
    }

    public static class RoutePoint{
        public static final String TBUS = "tbus";
        public static final String BBUB = "bbub";
        public static final String MBUB = "mbub";
        public static final String OBUB = "obub";
        public static final String TOBUS = "tobus";
        public static final String ABUB = "abub";
        public static final String APBUB = "apbub";
        public static final String NBUB = "nbub";
        public static final String GYEONGGI = "gyeonggi";
        public static final String INOHEON = "inoheon";
    }

    public static class InbOutB{
        public static final String INB = "inb";
        public static final String OUTB = "outb";
    }

}
