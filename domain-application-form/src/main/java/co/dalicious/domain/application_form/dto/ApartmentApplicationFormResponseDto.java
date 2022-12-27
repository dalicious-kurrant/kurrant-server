package co.dalicious.domain.application_form.dto;

import co.dalicious.domain.address.dto.CreateAddressResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "아파트 스팟 개설 신청 응답 DTO")
@Getter
@NoArgsConstructor
public class ApartmentApplicationFormResponseDto {
    private ApplyUserDto user;
    private CreateAddressResponseDto address;
    private ApartmentApplyInfoDto info;
    private List<ApplyMealInfoResponseDto> meal;
    private String memo;

    @Builder
    public ApartmentApplicationFormResponseDto(ApplyUserDto user, CreateAddressResponseDto address, ApartmentApplyInfoDto info, List<ApplyMealInfoResponseDto> meal, String memo) {
        this.user = user;
        this.address = address;
        this.info = info;
        this.meal = meal;
        this.memo = memo;
    }
}
