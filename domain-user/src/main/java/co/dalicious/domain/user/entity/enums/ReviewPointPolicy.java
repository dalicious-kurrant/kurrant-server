package co.dalicious.domain.user.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ReviewPointPolicy {

    REVIEW_RANGE_1("0", "~", "50", "70", 1)
//    REVIEW_RANGE_2("8000", "12999", "110", "210", 2),
//    REVIEW_RANGE_3("13000", "19999", "165", "265", 3),
//    REVIEW_RANGE_4("20000","~", "300", "400", 4)
    ;

    private final String minPrice;
    private final String maxPrice;
    private final String contentPoint;
    private final String imagePrice;
    private final Integer code;

    ReviewPointPolicy(String minPrice, String maxPrice, String contentPoint, String imagePrice, Integer code) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.contentPoint = contentPoint;
        this.imagePrice = imagePrice;
        this.code = code;
    }

    public static ReviewPointPolicy ofCode(Integer code) {
        return Arrays.stream(ReviewPointPolicy.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }
}
