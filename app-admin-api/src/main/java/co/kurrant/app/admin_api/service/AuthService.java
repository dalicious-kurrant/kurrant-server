package co.kurrant.app.admin_api.service;

import co.kurrant.app.admin_api.dto.user.LoginRequestDto;
import co.kurrant.app.admin_api.dto.user.LoginResponseDto;

public interface AuthService {
  LoginResponseDto login(LoginRequestDto dto);
}
