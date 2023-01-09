package co.dalicious.domain.application_form.dto.corporation;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "기업 스팟 개설 신청 스팟 정보 요청 DTO")
public class CorporationSpotRequestDto {
    private String spotName;
    private CreateAddressRequestDto address;
    private List<Integer> diningTypes;
}
