package co.dalicious.domain.application_form.dto;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "아파트 스팟 개설 신청 요청 DTO")
@Getter
@NoArgsConstructor
public class ApartmentApplicationFormRequestDto {
    private ApplyUserDto user;
    private CreateAddressRequestDto address;
    private ApartmentApplyInfoDto aprtmentInfo;
    private List<ApplyMealInfoRequestDto> meal;
    private String memo;
}
