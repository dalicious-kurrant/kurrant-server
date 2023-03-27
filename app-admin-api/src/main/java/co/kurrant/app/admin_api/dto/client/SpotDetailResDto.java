package co.kurrant.app.admin_api.dto.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "스팟 셍세조회 응답 DTO")
public class SpotDetailResDto {

    @Schema(description = "스팟 이름")
    private String spotName;
    @Schema(description = "그룹 아이디")
    private BigInteger groupId;

    private String managerName;
    private BigInteger managerId;
    private String managerPhone;
    private String spotType;
    private String mealType;
    private String mealDay;
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



}
