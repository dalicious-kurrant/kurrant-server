package co.dalicious.client.oauth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoLoginResponseDto {
    private Long id;
    private String connected_at;
    private Properties properties;
    private KaKaoAccount kakao_account;

    @Getter
    @NoArgsConstructor
    public static class Properties {
        String nickname;
        String profile_image;
        String thumbnail_image;
    }

    @Getter
    @NoArgsConstructor
    public static class KaKaoAccount {
        Boolean profile_needs_agreement;
        Profile profile;
        Boolean name_needs_agreement;
        String name;
        Boolean has_email;
        Boolean email_needs_agreement;
        Boolean is_email_valid;
        Boolean is_email_verified;
        String email;
        Boolean has_phone_number;
        Boolean phone_number_needs_agreement;
        String phone_number;
        Boolean has_birthday;
        Boolean birthday_needs_agreement;
        String birthday;
        String birthday_type;
    }

    @Getter
    @NoArgsConstructor
    public static class Profile {
        String nickname;
        String thumbnail_image_url;
        String profile_image_url;
        Boolean is_default_image;
    }

}
