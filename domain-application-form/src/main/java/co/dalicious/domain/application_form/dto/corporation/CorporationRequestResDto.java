package co.dalicious.domain.application_form.dto.corporation;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class CorporationRequestResDto {
    private BigInteger id;
    private String createDate;
    private String name;
    private String address;
    private String phone;
    private String memo;
    private Integer progressStatus;
}
