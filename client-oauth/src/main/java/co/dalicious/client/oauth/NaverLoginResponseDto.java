package co.dalicious.client.oauth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NaverLoginResponseDto {
    private String resultcode;
    private String message;
    private response response;

    @Getter
    @NoArgsConstructor
    public static class response {
        String id;
        String email;
        String mobile;
        String mobile_e164;
        String name;
    }
}
