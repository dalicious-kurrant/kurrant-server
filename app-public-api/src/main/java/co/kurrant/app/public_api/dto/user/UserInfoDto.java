package co.kurrant.app.public_api.dto.user;

import co.dalicious.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "마이페이지(홈)에서 유저 정보를 가져온다.")
@Getter
@NoArgsConstructor
public class UserInfoDto {
    private String gourmetType;
    private String avatar;
    private String name;
    private Boolean isMembership;
    private Integer membershipPeriod;
    private BigDecimal point;
    private Integer dailyMealCount;

    @Builder
    public UserInfoDto(User user, Integer membershipPeriod, Integer dailyMealCount) {
        this.gourmetType = user.getGourmetType().getGourmetType();
        this.avatar = (user.getAvatar() == null) ? null : user.getAvatar().getLocation();
        this.name = user.getName();
        this.isMembership = user.getIsMembership();
        this.point = user.getPoint();
        this.membershipPeriod = membershipPeriod;
        this.dailyMealCount = dailyMealCount;
    }
}
