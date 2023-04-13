package co.dalicious.client.alarm.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class AutoPushAlarmDto {

    @Getter
    @Setter
    public static class AutoPushAlarmList {
        private Integer status;
        private BigInteger id;
        private Integer condition;
        private String message;
        private String url;
    }

    @Getter
    @Setter
    public static class AutoPushAlarmMessageReqDto {
        private BigInteger id;
        private String message;
    }

    @Getter
    @Setter
    public static class AutoPushAlarmStatusReqDto {
        private BigInteger id;
        private Integer status;
    }

    @Getter
    @Setter
    public static class AutoPushAlarmUrlReqDto {
        private BigInteger id;
        private String url;
    }
}
