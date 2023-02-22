package co.dalicious.domain.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Schema(description = "엑셀 기업 가입 가능 리스트 저장 요청 DTO")
public class ClientExcelSaveDto {

    @Schema(description = "유저 번호")
    private BigInteger id;
    @Schema(description = "이메일")
    private String email;
    @Schema(description = "유저 이름")
    private String name;
    @Schema(description = "휴대폰 번호")
    private String phone;
    @Schema(description = "그룹 아이디")
    private BigInteger groupId;
    @Schema(description = "그룹 이름")
    private BigInteger groupName;

}
