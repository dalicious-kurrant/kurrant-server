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

    @Builder
    public ReviewDto(Integer satisfaction, String image, String content, Boolean forMakers) {
        this.satisfaction = satisfaction;
        this.image = image;
        this.content = content;
        this.forMakers = forMakers;
    }
}
