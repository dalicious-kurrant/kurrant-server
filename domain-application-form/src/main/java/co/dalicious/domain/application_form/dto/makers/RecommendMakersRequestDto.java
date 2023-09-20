package co.dalicious.domain.application_form.dto.makers;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class RecommendMakersRequestDto {
    private List<BigInteger> id;
    private String name;
    private String phone;
    private CreateAddressRequestDto address;
    private BigInteger spotId;
}
