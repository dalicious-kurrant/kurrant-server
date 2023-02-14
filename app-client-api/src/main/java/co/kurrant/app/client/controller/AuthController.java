package co.kurrant.app.client.controller;

import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import co.kurrant.app.client.dto.LoginRequestDto;
import co.kurrant.app.client.dto.LoginResponseDto;
import co.kurrant.app.client.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "1. Auth")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/auth")
@RestController
public class AuthController {

  private final AuthService authService;
 /*
  @Operation(summary = "로그인 API")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/login")
  public LoginResponseDto login(@Valid @RequestBody LoginRequestDto body) {
    log.debug("로그인>>>>>>");
    return authService.login(body);
  }

  */
}
