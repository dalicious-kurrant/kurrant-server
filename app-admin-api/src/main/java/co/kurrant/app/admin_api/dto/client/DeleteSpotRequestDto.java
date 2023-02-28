package co.kurrant.app.admin_api.dto.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Schema(description = "스팟 삭제 요청 DTO")
public class DeleteSpotRequestDto {

    private List<BigInteger> spotIdList;

}
