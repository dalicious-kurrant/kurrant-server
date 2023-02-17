package co.kurrant.app.client_api.service;

import co.kurrant.app.client_api.dto.LoginRequestDto;
import co.kurrant.app.client_api.dto.LoginResponseDto;

public interface AuthService {
  public LoginResponseDto login(LoginRequestDto body);
}
