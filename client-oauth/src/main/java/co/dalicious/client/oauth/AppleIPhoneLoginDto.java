package co.dalicious.client.oauth;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AppleIPhoneLoginDto {
    String user;
    String email;
    FullName fullName;
    List<String> authorizedScopes;
    String identityToken;
    String authorizationCode;
    Integer realUserStatus;
    String state;
    String nonce;
    Boolean autoLogin;

    @Getter
    @Setter
    public static class FullName {
        String namePrefix;
        String givenName;
        String familyName;
        String nickname;
        String middleName;
        String nameSuffix;
    }

}
