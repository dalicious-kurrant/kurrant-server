package co.kurrant.app.public_api.dto.user;

import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.user.entity.ProviderEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Schema(description = "마이페이지에서 내 정보 응답 DTO")
@Getter
@NoArgsConstructor
public class UserPersonalInfoDto {
    private String gourmetType;
    private Boolean hasGeneralProvider;
    private String name;
    private String email;
    private String avatar;
    private List<ProviderEmail> providerEmails;

    @Builder
    public UserPersonalInfoDto(String gourmetType, String name, String email, String avatar, List<ProviderEmail> providerEmails) {
        this.gourmetType = gourmetType;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.providerEmails = providerEmails;
    }

    public void hasGeneralProvider(Boolean hasGeneralProvider) {
        this.hasGeneralProvider = hasGeneralProvider;
    }
}
