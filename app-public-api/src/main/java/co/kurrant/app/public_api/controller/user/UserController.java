package co.kurrant.app.public_api.controller.user;

import co.dalicious.domain.user.dto.OrderDetailDto;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.public_api.dto.user.ChangeMarketingDto;
import co.kurrant.app.public_api.dto.user.ChangePasswordRequestDto;
import co.kurrant.app.public_api.dto.user.ChangePhoneRequestDto;
import co.kurrant.app.public_api.dto.user.UserInfoDto;
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

    @Operation(summary = "유저정보 가져오기", description = "로그인 한 유저의 정보를 불러온다.")
    @GetMapping("/me")
    public UserInfoDto userInfo(HttpServletRequest httpServletRequest) {
        return userService.getUserInfo(httpServletRequest);
    }

    @GetMapping("v1/users/me/order")
    public OrderDetailDto userOrderbyDate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate){
        return userService.findOrderByServiceDate(startDate, endDate);
    }

    @Operation(summary = "SNS 계정 연결 및 해제", description = "SNS 계정을 연결하거나 해제한다.")
    @GetMapping("/me/sns/{sns}")
    public void editSnsAccount(HttpServletRequest httpServletRequest, @PathVariable String sns){
        userService.editSnsAccount(httpServletRequest, sns);
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
