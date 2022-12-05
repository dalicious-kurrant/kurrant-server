package co.kurrant.app.public_api.controller.user;

import co.dalicious.client.external.sms.SmsMessageDto;
import co.dalicious.domain.user.dto.UserDto;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.user.entity.User;
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

@Tag(name = "2. User")
@RequestMapping(value = "/v1/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
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

    @Operation(summary = "유저정보 가져오기", description = "로그인 한 유저의 정보를 불러온다.")
    @GetMapping("/me")
    public UserInfoDto userInfo(HttpServletRequest httpServletRequest){
        return userService.getUserInfo(httpServletRequest);
    }
}
