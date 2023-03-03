package co.kurrant.app.makers_api.service;

import co.dalicious.client.core.dto.request.LoginTokenDto;
import co.kurrant.app.makers_api.dto.user.LoginRequestDto;
import co.kurrant.app.makers_api.dto.user.LoginResponseDto;
import co.kurrant.app.makers_api.dto.user.TokenDto;

public interface UserService {
    //로그인
    LoginResponseDto login(LoginRequestDto loginRequestDto);
//     Access Token 재발급
//    LoginTokenDto reissue(TokenDto reissueTokenDto);
//    // 로그아웃
//    void logout(TokenDto tokenDto);
}
