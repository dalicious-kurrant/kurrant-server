package co.kurrant.app.admin_api.service;

import co.kurrant.app.admin_api.dto.LoginRequestDto;
import co.kurrant.app.admin_api.dto.LoginResponseDto;

public interface AuthService {
  public LoginResponseDto login(LoginRequestDto dto);
}
