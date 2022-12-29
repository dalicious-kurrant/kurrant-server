package co.kurrant.app.public_api.controller.user;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.user.repository.MembershipRepository;
import co.kurrant.app.public_api.dto.user.*;
import co.kurrant.app.public_api.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "2. User")
@RequestMapping(value = "/v1/users/me")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "마이페이지 유저 가져오기", description = "로그인 한 유저 정보를 불러온다.")
    @GetMapping("")
    public UserInfoDto getUserInfo(HttpServletRequest httpServletRequest) {
        return userService.getUserInfo(httpServletRequest);
    }

    @Operation(summary = "마이페이지의 개인 정보 페이지에서 유저 정보 가져오기", description = "개인 정보 페이지에서 로그인 한 유저의 개인 정보를 불러온다.")
    @GetMapping("/personal")
    public UserPersonalInfoDto getPersonalUserInfo(HttpServletRequest httpServletRequest) {
        return userService.getPersonalUserInfo(httpServletRequest);
    }

    @Operation(summary = "홈 유저 정보 가져오기", description = "로그인 한 유저의 정보를 불러온다.")
    @GetMapping("/userInfo")
    public UserHomeResponseDto userHomeInfo(HttpServletRequest httpServletRequest) {
        return userService.getUserHomeInfo(httpServletRequest);
    }

    @Operation(summary = "아이디/비밀번호 설정", description = "로그인 한 유저의 정보를 불러온다.")
    @PostMapping("/setting/GENERAL")
    public ResponseMessage setEmailAndPassword(HttpServletRequest httpServletRequest, @RequestBody SetEmailAndPasswordDto setEmailAndPasswordDto) {
        userService.setEmailAndPassword(httpServletRequest, setEmailAndPasswordDto);
        return ResponseMessage.builder()
                .message("아이디/비밀번호 설정에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "SNS 계정 연결", description = "SNS 계정을 연결한다.")
    @PostMapping("/connecting/{sns}")
    public ResponseMessage connectSnsAccount(HttpServletRequest httpServletRequest, @RequestBody SnsAccessToken snsAccessToken, @PathVariable String sns) {
        userService.connectSnsAccount(httpServletRequest, snsAccessToken, sns);
        return ResponseMessage.builder()
                .message("SNS 계정 연결에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "SNS 계정 해지", description = "SNS 계정 연결을 해제한다.")
    @DeleteMapping("/disconnecting/{sns}")
    public ResponseMessage disconnectingSnsAccount(HttpServletRequest httpServletRequest, @PathVariable String sns) {
        userService.disconnectSnsAccount(httpServletRequest, sns);
        return ResponseMessage.builder()
                .message("SNS 계정 연결 해제에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "휴대폰 번호 변경", description = "로그인 한 유저의 비밀번호를 변경한다.")
    @PostMapping("/change/phone")
    public ResponseMessage changePhone(HttpServletRequest httpServletRequest,
                                          @RequestBody ChangePhoneRequestDto changePhoneRequestDto) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        userService.changePhoneNumber(httpServletRequest, changePhoneRequestDto);
        return ResponseMessage.builder()
                .message("휴대폰 번호 변경에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "비밀번호 변경", description = "로그인 한 유저의 비밀번호를 변경한다.")
    @PostMapping("/change/password")
    public ResponseMessage changePassword(HttpServletRequest httpServletRequest,
                                          @RequestBody ChangePasswordDto changePasswordRequestDto) {
        userService.changePassword(httpServletRequest, changePasswordRequestDto);
        return ResponseMessage.builder()
                .message("비밀번호 변경에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "알림 설정", description = "알림/마케팅 수신 정보 설정 동의 여부를 변경한다.")
    @GetMapping("/setting")
    public ResponseMessage getAlarmSetting(HttpServletRequest httpServletRequest) {
        MarketingAlarmResponseDto MarketingDto = userService.getAlarmSetting(httpServletRequest);
        return ResponseMessage.builder()
                .message("알림 설정 조회에 성공하였습니다.")
                .data(MarketingDto)
                .build();
    }

    @Operation(summary = "알림 설정", description = "알림/마케팅 수신 정보 설정 동의 여부를 변경한다.")
    @PostMapping("/setting")
    public ResponseMessage changeAlarmSetting(HttpServletRequest httpServletRequest, @RequestBody MarketingAlarmRequestDto marketingAlarmDto) {
        MarketingAlarmResponseDto changeMarketingDto = userService.changeAlarmSetting(httpServletRequest, marketingAlarmDto);
        return ResponseMessage.builder()
                .message("마케팅 수신 정보 변경에 성공하였습니다.")
                .data(changeMarketingDto)
                .build();
    }


}
