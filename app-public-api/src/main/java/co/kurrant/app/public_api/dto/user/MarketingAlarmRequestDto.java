package co.kurrant.app.public_api.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarketingAlarmRequestDto {
    private Integer code;
    private Boolean isActive;
}
