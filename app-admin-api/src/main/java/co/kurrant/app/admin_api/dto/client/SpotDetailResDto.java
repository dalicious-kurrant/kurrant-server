package co.kurrant.app.admin_api.dto.client;
import co.dalicious.domain.paycheck.entity.enums.CategoryPrice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Schema(description = "스팟 상세조회 응답 DTO")
public class SpotDetailResDto {

    @Schema(description = "스팟 이름")
    private String spotName;
    @Schema(description = "그룹 아이디")
    private BigInteger groupId;
    private String managerName;
    private BigInteger managerId;
    private String managerPhone;
    private String spotType;
    private String diningTypes;
    private String mealDay;
    private String supportDays;
    private String notSupportDays;
    private String zipCode;
    private Boolean isMembershipSupport;
    private String address1;
    private String address2;
    @Schema(description = "아침 지원금")
    private BigDecimal breakfastSupportPrice;
    private BigDecimal lunchSupportPrice;
    private BigDecimal dinnerSupportPrice;
    private String location;
    private Integer minPrice;
    private Integer maxPrice;
    private Boolean isSetting;
    private Boolean isGarbage;
    private Boolean isHotStorage;
    private String memo;
    private List<PrepaidCategory> prepaidCategoryList;
    private List<CategoryPrice> categoryPrices;

//    public SpotDetailResDto() {
//        List<CategoryPrice> categoryPrices1 = new ArrayList<>();
//        for (co.dalicious.domain.paycheck.entity.enums.CategoryPrice categoryPrice : co.dalicious.domain.paycheck.entity.enums.CategoryPrice.values()) {
//            categoryPrices.add(new CategoryPrice(categoryPrice));
//        }
//        this.categoryPrices = categoryPrices1;
//    }

    @Getter
    public static class CategoryPrice {
        private final Integer code;
        private final String category;
        private final Integer price;

        public CategoryPrice(co.dalicious.domain.paycheck.entity.enums.CategoryPrice categoryPrice) {
            this.code = categoryPrice.getCode();
            this.category = categoryPrice.getCategory();
            this.price = categoryPrice.getPrice().intValue();
        }
    }

    @Getter
    public static class PrepaidCategory {
        private String paycheckCategoryItem;
        private Integer count;
        private Integer price;
        private Integer totalPrice;
    }

}
