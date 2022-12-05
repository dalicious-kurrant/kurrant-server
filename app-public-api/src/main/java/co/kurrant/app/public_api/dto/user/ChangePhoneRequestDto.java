package co.kurrant.app.public_api.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangePhoneRequestDto {
    String phone;
    String key;

    @Builder
    public ChangePhoneRequestDto(String phone, String key) {
        this.phone = phone;
        this.key = key;
    }
}
