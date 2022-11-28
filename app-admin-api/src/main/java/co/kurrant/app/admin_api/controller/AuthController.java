package co.kurrant.app.admin_api.controller;

import javax.validation.Valid;

import co.kurrant.app.admin_api.dto.LoginRequestDto;
import co.kurrant.app.admin_api.dto.LoginResponseDto;
import co.kurrant.app.admin_api.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "1. Auth")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/auth")
@RestController
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "로그인", description = "로그인을 수행한다.")
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/login")
  public LoginResponseDto login(@Parameter(name = "로그인정보", description = "",
      required = true) @Valid @RequestBody LoginRequestDto dto) {
    return authService.login(dto);
  }
}
