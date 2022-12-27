package co.dalicious.domain.application_form.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "아파트 스팟 개설 신청 아파트 정보 DTO")
@Getter
@NoArgsConstructor
public class ApartmentApplyInfoDto {
    private String apartmentName;
    private String serviceStartDate;
    private Integer dongCount;
    private Integer familyCount;
}
