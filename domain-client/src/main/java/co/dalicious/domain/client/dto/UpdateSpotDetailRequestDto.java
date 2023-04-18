package co.dalicious.domain.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "스팟 상세정보를 수정 요청하는 Dto")
public class UpdateSpotDetailRequestDto {

    @Schema(description = "스팟 ID")
    private BigInteger spotId;
    @Schema(description = "스팟 이름")
    private String spotName;
    @Schema(description = "담당자 이름")
    private String managerName;
    @Schema(description = "담당자 ID")
    private BigInteger managerId;
    @Schema(description = "담당자 전화번호")
    private String managerPhone;
    @Schema(description = "스팟 타입")
    private String spotType;
    @Schema(description = "식사 타입")
    private String diningTypes;
    @Schema(description = "식사 요일")
    private String serviceDays;
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
    @Schema(description = "메모")
    private String memo;

}
