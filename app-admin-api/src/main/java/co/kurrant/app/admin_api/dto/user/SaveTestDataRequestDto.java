package co.kurrant.app.admin_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "백오피스에서 테스트 데이터를 넣는 DTO")
public class SaveTestDataRequestDto {

    private List<TestData> testData;


}
