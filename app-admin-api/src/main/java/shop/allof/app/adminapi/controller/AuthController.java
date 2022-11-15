package shop.allof.app.adminapi.controller;

import javax.validation.Valid;
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
import shop.allof.app.adminapi.dto.LoginRequestDto;
import shop.allof.app.adminapi.dto.LoginResponseDto;
import shop.allof.app.adminapi.service.AuthService;

@Tag(name = "1. Auth")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/auth")
@RestController
public class AuthController {
  @Autowired
  private AuthService authService;

  @Operation(summary = "로그인", description = "로그인을 수행한다.")
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/login")
  public LoginResponseDto login(@Parameter(name = "로그인정보", description = "",
      required = true) @Valid @RequestBody LoginRequestDto dto) {
    return authService.login(dto);
  }
}
