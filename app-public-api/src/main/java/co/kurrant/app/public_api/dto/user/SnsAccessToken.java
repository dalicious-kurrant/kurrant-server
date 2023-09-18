package co.kurrant.app.public_api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SnsAccessToken {
    private String snsAccessToken;
    private String fcmToken;
    private String version;
}
