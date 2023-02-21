package co.dalicious.domain.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Schema(description = "엑셀 파일 내보내기 요청 DTO")
public class ClientUserWaitingListSaveRequestDto {
    @Schema(description = "유저 번호")
    private List<BigInteger> id;
    @Schema(description = "이메일")
    private List<String> email;
    @Schema(description = "유저 이름")
    private List<String> name;
    @Schema(description = "휴대폰 번호")
    private List<String> phone;
    @Schema(description = "기업코드")
    private String code;
}
