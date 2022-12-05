package co.kurrant.app.public_api.dto.user;

import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.user.entity.ProviderEmail;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UserInfoDto {
    String gourmetType;
    String name;
    String email;
    Image avatar;
    List<ProviderEmail> providerEmails;
}
