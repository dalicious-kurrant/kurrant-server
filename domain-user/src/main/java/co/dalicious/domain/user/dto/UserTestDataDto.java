package co.dalicious.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Map;

@Getter
@Setter
public class UserTestDataDto {

    private BigInteger id;
    private Map<BigInteger, String> foodIds;
    private Integer page;

}
