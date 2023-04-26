package co.kurrant.app.admin_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "TestData를 수정 요청하는 Dto")
public class UpdateTestData {

    private BigInteger testDataId;
    private TestData testData;

}
