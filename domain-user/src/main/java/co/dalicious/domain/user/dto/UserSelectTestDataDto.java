package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "유저가 선택한 Test Data")
public class UserSelectTestDataDto {

    @Schema(description = "선호하는 음식 ID")
    private String selectedFoodId;

    @Schema(description = "선호하지 않는 음식 ID")
    private String unselectedFoodId;

}
