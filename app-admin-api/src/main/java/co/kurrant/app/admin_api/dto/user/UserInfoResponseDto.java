package co.kurrant.app.admin_api.dto.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "유저정보 응답 dTO")
public class UserInfoResponseDto {
    @Schema(description = "유저 아이디")
    private BigInteger id;
    @Schema(description = "비밀번호")
    private String password;
    @Schema(description = "이름")
    private String userName;
    @Schema(description = "유저타입")
    private String role;
    @Schema(description = "탈퇴 상태")
    private Integer status;
    @Schema(description = "휴대폰 번호")
    private String phone;
    @Schema(description = "이메일")
    private String email;
    @Schema(description = "그룹 이름")
    private String groupName;
    @Schema(description = "부서 이름")
    private String departmentName;
    @Schema(description = "보유 포인트")
    private Integer point;
    @Schema(description = "미식가 타입")
    private String gourmetType;
    @Schema(description = "멤버십 여부")
    private Boolean isMembership;
    @Schema(description = "이메일동의 여부")
    private String marketingAgreed;
    @Schema(description = "이메일 동의 날짜")
    private String marketingAgreedDateTime;
    @Schema(description = "혜택 및 소식 알림")
    private Boolean marketingAlarm;
    @Schema(description = "주문 알림 여부")
    private String userOrderAlarm;
    @Schema(description = "최근 로그인 날짜")
    private String recentLoginDateTime;
    @Schema(description = "생성일")
    private String userCreatedDateTime;
    @Schema(description = "수정일")
    private String userUpdatedDateTime;
    @Schema(description = "일반 이메일")
    private String generalEmail;
    @Schema(description = "카카오 이메일")
    private String kakaoEmail;
    @Schema(description = "네이버 이메일")
    private String naverEmail;
    @Schema(description = "페이스북 이메알")
    private String facebookEmail;
    @Schema(description = "애플 이메일")
    private String appleEmail;
    @Schema(description = "결제 비밀번호")
    private String paymentPassword;

}
