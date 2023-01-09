package co.dalicious.domain.application_form.dto.apartment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema(description = "아파트 스팟 개설 신청 아파트 정보 DTO")
@Getter
@Setter
public class ApartmentApplyInfoDto {
    private String apartmentName;
    private String serviceStartDate;
    private Integer dongCount;
    private Integer familyCount;
    private List<String> diningTypes;
}
