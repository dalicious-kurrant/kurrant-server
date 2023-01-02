package co.dalicious.client.oauth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SnsLoginResponseDto {
    private String email;
    private String phone;
    private String name;

    @Builder
    public SnsLoginResponseDto(String email, String phone, String name) {
        this.email = email;
        this.phone = phone;
        this.name = name;
    }
}
