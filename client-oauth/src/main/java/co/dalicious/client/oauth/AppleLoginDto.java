package co.dalicious.client.oauth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppleLoginDto {
    String id_token;
    String code;
    String nonce;
    String state;
    UserInfo user;

    @Getter
    @Setter
    public static class UserInfo {
        String email;
        FullName name;
    }

    @Getter
    @Setter
    public static class FullName {
        String firstName;
        String lastName;
    }
}
