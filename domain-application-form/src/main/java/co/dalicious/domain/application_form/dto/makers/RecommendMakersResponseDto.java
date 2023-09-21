package co.dalicious.domain.application_form.dto.makers;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class RecommendMakersResponseDto {
    private List<BigInteger> recommendIds;
    private String name;
    private Integer count;
    private Boolean IsRecommend;
    private String longitude;
    private String latitude;
}
