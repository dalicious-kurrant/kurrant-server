package co.dalicious.domain.application_form.dto.makers;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class UpdateRecommendMakersReqDto {
    private List<BigInteger> ids;
    private BigInteger spotId;
}
