package co.dalicious.domain.client.dto;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "아파트 개설 요청 DTO")
public class ApartmentRequestDto {
    private CreateAddressRequestDto address;
    private ApartmentInfo apartmentInfo;
    private List<Meal> meals;

    @Getter
    @Setter
    public static class ApartmentInfo {
        private String diningTypes;
        private String name;
        private Integer familyCount;
    }

    @Getter
    @Setter
    public static class Meal {
        private Integer diningType;
        private String deliveryTime;
        private String lastOrderTime;
        private String serviceDays;
    }

}
