package co.kurrant.app.public_api.dto.user;

import lombok.Getter;
import lombok.Setter;

public class NicknameDto {
    @Getter
    @Setter
    public static class Noun {
        private String noun;
    }
    @Getter
    @Setter
    public static class Adjective {
        private String adjective;
    }
}
