package co.dalicious.domain.application_form.dto.apartment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "아파트 스팟 개설 신청 아파트 정보 DTO")
@Getter
@NoArgsConstructor
public class ApartmentApplyInfoDto {
    private String apartmentName;
    private String serviceStartDate;
    private Integer dongCount;
    private Integer familyCount;
    private List<String> diningTypes;

    @Builder
    public ApartmentApplyInfoDto(String apartmentName, String serviceStartDate, Integer dongCount, Integer familyCount, List<String> diningTypes) {
        this.apartmentName = apartmentName;
        this.serviceStartDate = serviceStartDate;
        this.dongCount = dongCount;
        this.familyCount = familyCount;
        this.diningTypes = diningTypes;
    }
}
