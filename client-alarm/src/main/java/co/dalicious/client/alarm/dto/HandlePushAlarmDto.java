package co.dalicious.client.alarm.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class HandlePushAlarmDto {

    @Getter
    @Setter
    public static class HandlePushAlarmReqDto {
        private Integer type;
        private List<BigInteger> groupIds;
        private List<BigInteger> spotIds;
        private List<BigInteger> userIds;
        private String message;
        private String page;
    }

    @Getter
    @Setter
    public static class HandlePushAlarmType {
        private Integer code;
        private String type;
    }

    @Getter
    @Setter
    public static class HandlePushAlarm {
        private BigInteger id;
        private String name;
        private String email;
    }
}
