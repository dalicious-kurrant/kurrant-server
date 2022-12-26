package co.kurrant.app.public_api.controller.user;

import co.dalicious.domain.order.dto.OrderCartDto;
import co.dalicious.domain.user.dto.OrderDetailDto;
import co.dalicious.client.core.dto.response.ResponseMessage;
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

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Tag(name = "2. User")
@RequestMapping(value = "/v1/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "홈 유저 정보 가져오기", description = "로그인 한 유저의 정보를 불러온다.")
    @GetMapping("/userInfo")
    public UserHomeResponseDto userHomeInfo(HttpServletRequest httpServletRequest) {
        return userService.getUserHomeInfo(httpServletRequest);
    }

    @Operation(summary = "유저정보 가져오기", description = "로그인 한 유저의 정보를 불러온다.")
    @GetMapping("/me")
    public UserInfoDto userInfo(HttpServletRequest httpServletRequest) {
        return userService.getUserInfo(httpServletRequest);
    }


    @Operation(summary = "SNS 계정 연결", description = "SNS 계정을 연결한다.")
    @PostMapping("/me/connecting/{sns}")
    public ResponseMessage connectSnsAccount(HttpServletRequest httpServletRequest, @RequestBody SnsAccessToken snsAccessToken, @PathVariable String sns) {
        userService.connectSnsAccount(httpServletRequest, snsAccessToken, sns);
        return ResponseMessage.builder()
                .message("SNS 계정 연결에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "SNS 계정 해지", description = "SNS 계정 연결을 해제한다.")
    @DeleteMapping("/me/disconnecting/{sns}")
    public ResponseMessage disconnectingSnsAccount(HttpServletRequest httpServletRequest, @PathVariable String sns) {
        userService.disconnectSnsAccount(httpServletRequest, sns);
        return ResponseMessage.builder()
                .message("SNS 계정 연결 해제에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "휴대폰 번호 변경", description = "로그인 한 유저의 비밀번호를 변경한다.")
    @PostMapping("/change/phone")
    public ResponseMessage changePassword(HttpServletRequest httpServletRequest,
                                          @RequestBody ChangePhoneRequestDto changePhoneRequestDto) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        userService.changePhoneNumber(httpServletRequest, changePhoneRequestDto);
        return ResponseMessage.builder()
                .message("휴대폰 번호 변경에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "이메일/비밀번호 설정", description = "로그인 한 유저(소셜로그인 유저)의 이메일과 비밀번호를 설정한다.")
    @PostMapping("/setting/email")
    public ResponseMessage setEmailAndPassword(HttpServletRequest httpServletRequest) {
        return ResponseMessage.builder()
                .message("휴대폰 번호 변경에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "비밀번호 변경", description = "로그인 한 유저의 비밀번호를 변경한다.")
    @PostMapping("/change/password")
    public ResponseMessage changePassword(HttpServletRequest httpServletRequest,
                                          @RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        userService.changePassword(httpServletRequest, changePasswordRequestDto);
        return ResponseMessage.builder()
                .message("비밀번호 변경에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "알림 설정", description = "알림/마케팅 수신 정보 설정 동의 여부를 변경한다.")
    @PostMapping("/me/setting")
    public ResponseMessage changeAlarmSetting(HttpServletRequest httpServletRequest,
                                              @RequestParam(required = false) Boolean isMarketingInfoAgree,
                                              @RequestParam(required = false) Boolean isMarketingAlarmAgree,
                                              @RequestParam(required = false) Boolean isOrderAlarmAgree) {
        ChangeMarketingDto changeMarketingDto = userService.changeAlarmSetting(httpServletRequest, isMarketingInfoAgree, isMarketingAlarmAgree, isOrderAlarmAgree);
        return ResponseMessage.builder()
                .message("마케팅 수신 정보 변경에 성공하였습니다.")
                .data(changeMarketingDto)
                .build();
    }


}
