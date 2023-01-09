package co.dalicious.domain.application_form.dto.apartment;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.application_form.dto.ApplyUserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema(description = "아파트 스팟 개설 신청 요청 DTO")
@Getter
@Setter
public class ApartmentApplicationFormRequestDto {
    private ApplyUserDto user;
    private CreateAddressRequestDto address;
    private ApartmentApplyInfoDto apartmentInfo;
    private List<ApartmentMealInfoRequestDto> mealDetails;
    private String memo;
}
