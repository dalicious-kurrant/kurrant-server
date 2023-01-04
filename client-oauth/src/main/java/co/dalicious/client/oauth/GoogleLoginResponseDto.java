package co.dalicious.client.oauth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleLoginResponseDto {
    private String id;
    private String email;
    private Boolean verified_email;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
    private String locale;
    private String hd;
}
