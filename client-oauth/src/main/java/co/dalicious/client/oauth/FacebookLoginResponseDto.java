package co.dalicious.client.oauth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacebookLoginResponseDto {
    private String id;
    private String name;
    private String email;
}
