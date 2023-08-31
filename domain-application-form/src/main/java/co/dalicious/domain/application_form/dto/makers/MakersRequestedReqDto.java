package co.dalicious.domain.application_form.dto.makers;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class MakersRequestedReqDto {
    private BigInteger id;
    private String name;
    private String makersName;
    private String address;
    private String phone;
    private String memo;
    private String mainProduct;
    private String progressStatus;
}
