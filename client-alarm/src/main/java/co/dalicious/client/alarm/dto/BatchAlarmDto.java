package co.dalicious.client.alarm.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Map;

@Getter
@Setter
public class BatchAlarmDto {
    private Map<String, BigInteger> tokenList;
    private String title;
    private String page;
    private String message;
}
