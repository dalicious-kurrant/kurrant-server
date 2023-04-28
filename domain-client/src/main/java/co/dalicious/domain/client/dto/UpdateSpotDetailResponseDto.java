package co.dalicious.domain.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Schema(description = "스팟 상세정보를 수정 요청하는 Dto")
public class UpdateSpotDetailResponseDto {
    @Schema(description = "스팟 ID")
    private BigInteger spotId;
    @Schema(description = "코드")
    private String code;
    @Schema(description = "스팟 이름")
    private String spotName;
    @Schema(description = "담당자 이름")
    private String managerName;
    @Schema(description = "담당자 ID")
    private BigInteger managerId;
    @Schema(description = "사원수")
    private Integer employeeCount;
    @Schema(description = "담당자 전화번호")
    private String managerPhone;
    @Schema(description = "스팟 타입")
    private String spotType;
    @Schema(description = "식사 타입")
    private String diningTypes;
    @Schema(description = "식사 요일")
    private String serviceDays;
    @Schema(description = "지원금이 있는 요일")
    private String supportDays;
    @Schema(description = "지원금이 없는 요일")
    private String notSupportDays;
    @Schema(description = "우편 번호")
    private String zipCode;
    @Schema(description = "기업 멤버십 지원 여부")
    private Boolean isMembershipSupport;
    @Schema(description = "기본 주소")
    private String address1;
    @Schema(description = "상세주소")
    private String address2;
    @Schema(description = "아침 지원금")
    private BigDecimal breakfastSupportPrice;
    @Schema(description = "점심 지원금")
    private BigDecimal lunchSupportPrice;
    @Schema(description = "저녁 지원금")
    private BigDecimal dinnerSupportPrice;
    @Schema(description = "좌표")
    private String location;
    @Schema(description = "최소 구매 가능 금액")
    private BigDecimal minPrice;
    @Schema(description = "최대 구매 가능 금액")
    private BigDecimal maxPrice;
    @Schema(description = "식사 세팅 지원 서비스")
    private Boolean isSetting;
    @Schema(description = "쓰레기 지원 서비스")
    private Boolean isGarbage;
    @Schema(description = "온장고 대여 서비스")
    private Boolean isHotStorage;
    @Schema(description = "선불정산 여부 체크")
    private Boolean isPrepaid;
    @Schema(description = "메모")
    private String memo;
    @Schema(description = "정산 선불 정보")
    private List<PrepaidCategory> prepaidCategoryList;
    private List<CategoryPrice> categoryPrices;

    public UpdateSpotDetailResponseDto() {
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
    @NoArgsConstructor
    public static class PrepaidCategory {
        private Integer code;
        private Integer count;
        private Integer price;
        private Integer totalPrice;
        @Builder
        public PrepaidCategory(Integer code, Integer count, Integer price, Integer totalPrice) {
            this.code = code;
            this.count = count;
            this.price = price;
            this.totalPrice = totalPrice;
        }
    }
}
