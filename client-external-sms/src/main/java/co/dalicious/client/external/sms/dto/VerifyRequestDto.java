package co.dalicious.client.external.sms.dto;

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
