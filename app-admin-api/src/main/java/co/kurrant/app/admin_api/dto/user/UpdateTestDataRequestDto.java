package co.kurrant.app.admin_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "테스트 데이터를 수정 요청하는 Dto")
public class UpdateTestDataRequestDto {

    List<UpdateTestData> updateTestDataList;

}
