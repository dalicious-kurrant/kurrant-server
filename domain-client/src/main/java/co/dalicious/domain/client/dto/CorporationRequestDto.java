package co.dalicious.domain.client.dto;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Schema(description = "그룹 개설 요청 DTO")
public class CorporationRequestDto {
    private CreateAddressRequestDto address;
    private CorporationInfo corporationInfo;
    private List<Meal> meals;

    @Getter
    @Setter
    public static class CorporationInfo {
        private String diningTypes;
        private String name;
        private Integer employeeCount;
    }

    @Getter
    @Setter
    public static class Meal {
        private Integer diningType;
        private BigDecimal supportPrice;
        private String deliveryTime;
        private String lastOrderTime;
        private String serviceDays;
    }

}
