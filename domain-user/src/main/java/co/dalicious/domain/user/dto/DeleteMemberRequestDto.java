package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Schema(description = "멤버 탈퇴요청 DTO")
public class DeleteMemberRequestDto {

    @Schema(description = "탈퇴처리할 유저의 아이디리스트")
    private List<BigInteger> userIdList;
    @Schema(description = "탈퇴처리할 그룹 아이디")
    private BigInteger groupId;

}
