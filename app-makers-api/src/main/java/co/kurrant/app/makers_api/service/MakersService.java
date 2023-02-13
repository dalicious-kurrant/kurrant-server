package co.kurrant.app.makers_api.service;

import co.dalicious.client.core.dto.request.LoginTokenDto;
import co.kurrant.app.makers_api.dto.LoginRequestDto;
import co.kurrant.app.makers_api.dto.LoginResponseDto;
import co.kurrant.app.makers_api.dto.TokenDto;

public interface MakersService {
    //로그인
    LoginResponseDto login(LoginRequestDto loginRequestDto);
    // Access Token 재발급
    LoginTokenDto reissue(TokenDto reissueTokenDto);
    // 로그아웃
    void logout(TokenDto tokenDto);
}
