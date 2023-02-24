package co.kurrant.app.admin_api.dto.client;

import co.dalicious.domain.client.dto.SpotResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "스팟정보 저장을 위한 DTO")
public class SaveSpotList {
    List<SpotResponseDto> saveSpotList;
}
