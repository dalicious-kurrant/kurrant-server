package co.dalicious.data.redis.sms;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerifyRequestDto {
    String phone;
    String certificationNumber;

    public VerifyRequestDto(String phone, String certificationNumber) {
        this.phone = phone;
        this.certificationNumber = certificationNumber;
    }
}
