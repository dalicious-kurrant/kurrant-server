package co.kurrant.app.client_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Schema(description = "가입 가능 리스트 삭제 요청 DTO")
public class DeleteWaitingMemberRequestDto {

    private List<BigInteger> waitMemberIdList;

}
