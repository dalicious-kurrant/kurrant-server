package co.dalicious.domain.application_form.dto.makers;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class AdminRecommendMakersResDto {
    private BigInteger id;
    private Integer status;
    private String name;
    private String address;
    private String phone;
    private String groupName;
    private Integer count;
}
