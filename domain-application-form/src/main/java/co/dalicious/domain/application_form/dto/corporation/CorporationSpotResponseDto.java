package co.dalicious.domain.application_form.dto.corporation;

import co.dalicious.domain.address.dto.CreateAddressResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "기업 스팟 개설 신청 스팟 정보 응답 DTO")
public class CorporationSpotResponseDto {
    private String spotName;
    private CreateAddressResponseDto address;
    private List<String> diningTypes;
}
