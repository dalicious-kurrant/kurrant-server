package co.dalicious.domain.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class ReviewableItemListDto {
    private BigInteger orderItemId;
    private String diningType;
    private String imageLocation;
    private String makersName;
    private String foodName;
    private long reviewDDay;

    @Builder
    public ReviewableItemListDto(BigInteger orderItemId, String diningType, String imageLocation, String makersName, String foodName, long reviewDDAy) {
        this.orderItemId = orderItemId;
        this.diningType = diningType;
        this.imageLocation = imageLocation;
        this.makersName = makersName;
        this.foodName = foodName;
        this.reviewDDay = reviewDDAy;
    }

}
