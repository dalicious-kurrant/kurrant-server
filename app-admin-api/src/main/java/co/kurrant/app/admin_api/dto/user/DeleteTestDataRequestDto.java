package co.kurrant.app.admin_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Schema(description = "TestData 삭제 요청 Dto")
public class DeleteTestDataRequestDto {
    private List<BigInteger> testDataIdList;
}
