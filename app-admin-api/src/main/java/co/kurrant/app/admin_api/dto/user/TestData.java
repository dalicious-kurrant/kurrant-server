package co.kurrant.app.admin_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Schema(description = "TestData를 저장하는 DTO")
public class TestData {

    private List<BigInteger> foodIds;
    private Integer pageNum;

}
