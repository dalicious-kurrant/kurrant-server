package co.dalicious.domain.client.dto;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ApartmentRequestDto {
    private CreateAddressRequestDto address;
    private ApartmentInfo apartmentInfo;
    private List<Meal> meals;

    @Getter
    @NoArgsConstructor
    public static class ApartmentInfo {
        private String diningTypes;
        private String name;
        private Integer familyCount;
    }

    @Getter
    @NoArgsConstructor
    public static class Meal {
        private Integer diningType;
        private String deliveryTime;
        private String lastOrderTime;
        private String serviceDays;
    }

}
