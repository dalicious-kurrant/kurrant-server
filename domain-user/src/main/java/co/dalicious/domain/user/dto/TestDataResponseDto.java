package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "테스트데이터 조회를 위한 DTO")
public class TestDataResponseDto {

    private String foodIds;
    private Integer pageNum;
}
