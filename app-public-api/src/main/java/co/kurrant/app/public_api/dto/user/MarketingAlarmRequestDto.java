package co.kurrant.app.public_api.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MarketingAlarmRequestDto {
    private Boolean isMarketingInfoAgree;
    private Boolean isMarketingAlarmAgree;
    private Boolean isOrderAlarmAgree;
}
