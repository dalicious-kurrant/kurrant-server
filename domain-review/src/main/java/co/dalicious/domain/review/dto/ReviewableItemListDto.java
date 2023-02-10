package co.dalicious.domain.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class ReviewableItemListDto {
    private BigInteger itemId;
    private String diningType;
    private String serviceDate;
    private String imageLocation;
    private String makersName;
    private String foodName;
    private long reviewDDay;

    @Builder
    public ReviewableItemListDto(BigInteger itemId, String diningType, String serviceDate, String imageLocation, String makersName, String foodName, long reviewDDAy) {
        this.itemId = itemId;
        this.diningType = diningType;
        this.serviceDate = serviceDate;
        this.imageLocation = imageLocation;
        this.makersName = makersName;
        this.foodName = foodName;
        this.reviewDDay = reviewDDAy;
    }

}
