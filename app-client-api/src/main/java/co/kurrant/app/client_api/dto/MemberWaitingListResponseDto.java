package co.kurrant.app.client_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "가입대기중인 유저 목록 조회 응답 DTO")
public class MemberWaitingListResponseDto {
    @Schema(description = "userId")
    private BigInteger id;
    @Schema(description = "이메일")
    private String email;
    @Schema(description = "이름")
    private String name;
    @Schema(description = "휴대폰 번호")
    private String phone;
    @Schema(description = "가입 상태")
    private String status;
}
