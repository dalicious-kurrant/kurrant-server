package co.kurrant.app.admin_api.dto.client;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
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

    @Schema(description = "그룹 아이디")
    private BigInteger groupId;
    @Schema(description = "기업 코드")
    private String code;
    @Schema(description = "담당자")
    private String managerName;
    @Schema(description = "담당자 번호")
    private String managerPhone;
    @Schema(description = "스팟 타입")
    private String spotType;
    @Schema(description = "담당자 ID")
    private BigInteger managerId;
    @Schema(description = "사원수")
    private Integer expectedCount;
    @Schema(description = "이름")
    private String spotName;
    @Schema(description = "식사타입")
    private String diningTypes;
    @Schema(description = "식사요일")
    private String mealDay;
    @Schema(description = "우편번호")
    private String zipCode;
    @Schema(description = "기업 멤버십 지원 여부")
    private Boolean isMembershipSupport;
    @Schema(description = "기본 주소")
    private String address1;
    @Schema(description = "상세 주소")
    private String address2;
    @Schema(description = "위치")
    private String location;
    @Schema(description = "아침 지원금")
    private BigDecimal breakfastSupportPrice;
    private BigDecimal lunchSupportPrice;
    private BigDecimal dinnerSupportPrice;
    private String supportDays;
    private String notSupportDays;
    private Integer minPrice;
    private Integer maxPrice;
    private Boolean isSetting;
    private Boolean isGarbage;
    private Boolean isHotStorage;
    private Boolean isPrepaid;
    private String memo;
    private List<PrepaidCategory> prepaidCategoryList;
    private List<CategoryPrice> categoryPrices;

    public SpotDetailResDto() {
        List<CategoryPrice> categoryPrices1 = new ArrayList<>();
        for (co.dalicious.system.enums.CategoryPrice categoryPrice : co.dalicious.system.enums.CategoryPrice.values()) {
            categoryPrices1.add(new CategoryPrice(categoryPrice));
        }
        this.categoryPrices = categoryPrices1;
    }

    @Getter
    public static class CategoryPrice {
        private final Integer code;
        private final String category;
        private final Integer price;

        public CategoryPrice(co.dalicious.system.enums.CategoryPrice categoryPrice) {
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

        @Builder
        public PrepaidCategory(String paycheckCategoryItem, Integer count, Integer price, Integer totalPrice) {
            this.paycheckCategoryItem = paycheckCategoryItem;
            this.count = count;
            this.price = price;
            this.totalPrice = totalPrice;
        }
    }

}
