package co.kurrant.app.public_api.dto.user;

import co.dalicious.domain.user.dto.ProviderEmailDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Schema(description = "마이페이지에서 내 정보 응답 DTO")
@Getter
@Setter
public class UserPersonalInfoDto {
    private String gourmetType;
    private Boolean hasGeneralProvider;
    private String name;
    private String nickname;
    private String email;
    private String avatar;
    private List<ProviderEmailDto> providerEmails;
    private Integer membershipPeriod;

    public void hasGeneralProvider(Boolean hasGeneralProvider) {
        this.hasGeneralProvider = hasGeneralProvider;
    }
}
