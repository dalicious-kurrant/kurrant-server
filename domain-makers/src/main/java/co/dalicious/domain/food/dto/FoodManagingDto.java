package co.dalicious.domain.food.dto;

import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.food.converter.FoodStatusConverter;
import co.dalicious.domain.food.entity.FoodCapacity;
import co.dalicious.domain.food.entity.FoodDiscountPolicy;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.enums.FoodStatus;
import co.dalicious.system.util.converter.FoodTagsConverter;
import co.dalicious.system.util.enums.DiningType;
import co.dalicious.system.util.enums.FoodTag;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
public class FoodManagingDto {

    private String makersName;
    private BigInteger foodId;
    private String foodName;
    private BigDecimal foodPrice;
    private String foodImage;
    private BigDecimal makersDiscountPrice;
    private Integer makersDiscountRate;
    private BigDecimal periodDiscountPrice;
    private Integer periodDiscountRate;
    private List<Integer> foodTags;
    private String description;
    private BigDecimal customPrice;

    public FoodManagingDto(
            String makersName, BigInteger foodId, String foodName, BigDecimal foodPrice, String foodImage,
            BigDecimal makersDiscountPrice, Integer makersDiscountRate, BigDecimal periodDiscountPrice, Integer periodDiscountRate,
            List<Integer> foodTags, String description, BigDecimal customPrice) {

        this.makersName = makersName;
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodImage = foodImage;
        this.makersDiscountPrice = makersDiscountPrice;
        this.makersDiscountRate = makersDiscountRate;
        this.periodDiscountPrice = periodDiscountPrice;
        this.periodDiscountRate = periodDiscountRate;
        this.foodTags = foodTags;
        this.description = description;
        this.customPrice = customPrice;
    }
}
