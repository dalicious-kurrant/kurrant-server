package co.dalicious.domain.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class ReviewableItemListDto {
    private BigInteger orderItemId;
    private BigInteger dailyFoodId;
    private String diningType;
    private String imageLocation;
    private String makersName;
    private String foodName;
    private String foodDescription;
    private Integer foodCount;
    private String reviewDDay;
}
