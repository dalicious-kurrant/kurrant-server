package co.kurrant.app.admin_api.dto.user;

import co.dalicious.domain.user.entity.ProviderEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Schema(description = "변경된 유저정보를 저장 요청 하는 Dto")
public class SaveUserListRequestDto {

    private BigInteger userId;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String role;
    private Integer status;
    private String groupName;
    private Integer point;
    private Integer gourmetType;
    private Boolean isMembership;
    private Boolean marketingAgree;
    private String marketingAgreedDateTime;
    private Boolean marketingAlarm;
    private Boolean userOrderAlarm;
    private String recentLoginDateTime;
    private String userCreatedDateTime;
//    private List<ProviderEmail> ProviderEmails;


}
