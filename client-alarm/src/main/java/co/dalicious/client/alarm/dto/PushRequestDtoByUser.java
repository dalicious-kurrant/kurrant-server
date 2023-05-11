package co.dalicious.client.alarm.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class PushRequestDtoByUser {
    private String token;
    private String message;
    private String title;
    private String page;
}
