package co.kurrant.app.makers_api.service;

import co.kurrant.app.makers_api.dto.LoginRequestDto;
import co.kurrant.app.makers_api.dto.LoginResponseDto;

public interface UserService {
    //로그인
    LoginResponseDto login(LoginRequestDto loginRequestDto);
//     Access Token 재발급
//    LoginTokenDto reissue(TokenDto reissueTokenDto);
//    // 로그아웃
//    void logout(TokenDto tokenDto);
}
