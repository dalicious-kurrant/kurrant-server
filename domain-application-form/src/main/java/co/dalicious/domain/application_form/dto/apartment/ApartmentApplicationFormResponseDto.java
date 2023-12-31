package co.dalicious.domain.application_form.dto.apartment;

import co.dalicious.domain.address.dto.CreateAddressResponseDto;
import co.dalicious.domain.application_form.dto.ApplyUserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema(description = "아파트 스팟 개설 신청 응답 DTO")
@Getter
@Setter
public class ApartmentApplicationFormResponseDto {
    private String date;
    private Integer progressStatus;
    private ApplyUserDto user;
    private CreateAddressResponseDto address;
    private ApartmentApplyInfoDto info;
    private List<ApartmentMealInfoResponseDto> meal;
    private String memo;
    private String rejectedReason;

    @Builder
    public ApartmentApplicationFormResponseDto(String date, Integer progressStatus, ApplyUserDto user, CreateAddressResponseDto address, ApartmentApplyInfoDto info, List<ApartmentMealInfoResponseDto> meal, String memo, String rejectedReason) {
        this.date = date;
        this.progressStatus = progressStatus;
        this.user = user;
        this.address = address;
        this.info = info;
        this.meal = meal;
        this.memo = memo;
        this.rejectedReason = rejectedReason;
    }
}
