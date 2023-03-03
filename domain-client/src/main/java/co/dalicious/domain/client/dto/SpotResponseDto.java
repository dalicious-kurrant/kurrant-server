package co.dalicious.domain.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "스팟 정보 조회 응답용 DTO")
public class SpotResponseDto {

    @Schema(description = "스팟 아이디")
    private BigInteger spotId;
    @Schema(description = "스팟 상태")
    private Integer status;
    @Schema(description = "스팟 이름")
    private String spotName;
    @Schema(description = "그룹 아이디")
    private BigInteger groupId;
    @Schema(description = "그룹 이름")
    private String groupName;
    @Schema(description = "우편 번호")
    private String zipCode;
    @Schema(description = "기본 주소")
    private String address1;
    @Schema(description = "상세 주소")
    private String address2;
    @Schema(description = "위치")
    private String location;
    @Schema(description = "식사 타입(아침,점심,저녁)")
    private String diningType;
    @Schema(description = "아침 주문 마감시간")
    private String breakfastLastOrderTime;
    @Schema(description = "아침 배송시간")
    private String breakfastDeliveryTime;
    @Schema(description = "주문요일 아침")
    private String breakfastUseDays;
    @Schema(description = "아침 지원금")
    private BigDecimal breakfastSupportPrice;
    @Schema(description = "점심 주문 마감시간")
    private String lunchLastOrderTime;
    @Schema(description = "점심 배송시간")
    private String lunchDeliveryTime;
    @Schema(description = "점심 주문요일")
    private String lunchUseDays;
    @Schema(description = "점심 지원금")
    private BigDecimal lunchSupportPrice;
    @Schema(description = "저녁 주문 마감시간")
    private String dinnerLastOrderTime;
    @Schema(description = "저녁 배송시간")
    private String dinnerDeliveryTime;
    @Schema(description = "저녁 주문요일")
    private String dinnerUseDays;
    @Schema(description = "저녁 지원금")
    private BigDecimal dinnerSupportPrice;
    @Schema(description = "생성일")
    private String createdDateTime;
    @Schema(description = "수정일")
    private String updatedDateTime;



}
