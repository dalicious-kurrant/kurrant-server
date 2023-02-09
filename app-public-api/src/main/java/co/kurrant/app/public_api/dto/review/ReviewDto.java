package co.kurrant.app.public_api.dto.review;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class ReviewDto {
    private Integer satisfaction;
    private String image;
    private String content;
    private Boolean forMakers;
}
