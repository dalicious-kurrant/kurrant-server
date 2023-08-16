package co.kurrant.app.client_api.dto;

import co.dalicious.domain.user.entity.enums.GourmetType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "유저정보 응답 DTO")
public class MemberListResponseDto {
    @Schema(description = "유저 고유 ID")
    private BigInteger id;
    @Schema(description = "유저 앱 Id")
    private String userId;
    @Schema(description = "비밀번호")
    private String password;
    @Schema(description = "이름")
    private String name;
    @Schema(description = "닉네임")
    private String nickname;
    @Schema(description = "유저 타입 (0: 일반, 1: 관리자)")
    private String userType;
    @Schema(description = "휴대폰 번호")
    private String phone;
    @Schema(description = "이메일 주소")
    private String email;
    @Schema(description = "그룹이름")
    private String groupName;
    @Schema(description = "보유 포인트")
    private Integer point;
    @Schema(description = "미식가 타입")
    private GourmetType gourmetType;
    @Schema(description = "멤버십 여부")
    private Boolean isMembership;
    @Schema(description = "메모")
    private String memo;
}
