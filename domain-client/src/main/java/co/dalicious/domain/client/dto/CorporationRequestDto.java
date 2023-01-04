package co.dalicious.domain.client.dto;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class CorporationRequestDto {
    private CreateAddressRequestDto address;
    private CorporationInfo corporationInfo;
    private List<Meal> meals;

    @Getter
    @NoArgsConstructor
    public static class CorporationInfo {
        private String diningTypes;
        private String name;
        private Integer employeeCount;
    }

    @Getter
    @NoArgsConstructor
    public static class Meal {
        private Integer diningType;
        private BigDecimal supportPrice;
        private String deliveryTime;
        private String lastOrderTime;
        private String serviceDays;
    }

}
