package co.kurrant.app.makers_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.makers_api.dto.user.LoginRequestDto;
import co.kurrant.app.makers_api.dto.user.TokenDto;
import co.kurrant.app.makers_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@RestController
@RequestMapping(value = "/v1/makers")
@RequiredArgsConstructor
public class AuthController {

    private final UserService makersService;

    @Operation(summary = "로그인", description = "로그인을 수행한다.")
    @PostMapping("/login")
    public ResponseMessage login(@RequestBody LoginRequestDto loginRequestDto) {
        Date date = new Date();
        System.out.println(new Date(date.getTime()).getTime());
        return ResponseMessage.builder()
                .message("로그인에 성공했습니다.")
                .data(makersService.login(loginRequestDto))
                .build();
    }

//    @Operation(summary = "토큰 재발급", description = "토큰을 재발급한다.")
//    @PostMapping("/reissue")
//    public ResponseMessage reissue(@RequestBody TokenDto dto) {
//        return ResponseMessage.builder()
//                .message("토큰 재발급에 성공하였습니다.")
//                .data(makersService.reissue(dto))
//                .build();
//    }
//
//    @Operation(summary = "로그아웃", description = "로그아웃을 수행한다.")
//    @PostMapping("/logout")
//    public ResponseMessage logout(@RequestBody TokenDto dto) {
//        makersService.logout(dto);
//        return ResponseMessage.builder()
//                .message("로그아웃 되었습니다.")
//                .build();
//    }
}
