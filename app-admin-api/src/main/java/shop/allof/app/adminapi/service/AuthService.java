package shop.allof.app.adminapi.service;

import shop.allof.app.adminapi.dto.LoginRequestDto;
import shop.allof.app.adminapi.dto.LoginResponseDto;

public interface AuthService {
  public LoginResponseDto login(LoginRequestDto dto);
}
