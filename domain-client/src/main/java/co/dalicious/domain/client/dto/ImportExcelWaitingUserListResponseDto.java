package co.dalicious.domain.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "엑셀로 가입대기 유저를 불러오는 응답 DTO")
public class ImportExcelWaitingUserListResponseDto {

    @Schema(description = "유저id(앱ID 아님)")
    private BigInteger id;
    @Schema(description = "이메일")
    private String email;
    @Schema(description = "이름")
    private String name;
    @Schema(description = "휴대폰번호")
    private String phone;
    @Schema(description = "고객사 ID")
    private BigInteger corporationId;

}
