package co.kurrant.app.public_api.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
//@NoArgsConstructor
@Setter
public class MarketingAlarmRequestDto {
    private Boolean isMarketingInfoAgree;
    private Boolean isMarketingAlarmAgree;
    private Boolean isOrderAlarmAgree;
//    private Integer code;
//    private Boolean isActive;
}
