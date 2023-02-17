package co.kurrant.app.client_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.client_api.dto.LoginRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import co.kurrant.app.client_api.service.AuthService;
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

  @Operation(summary = "로그인", description = "로그인을 수행한다.")
  @PostMapping("/login")
  public ResponseMessage login(@RequestBody LoginRequestDto dto) {
    return ResponseMessage.builder()
            .message("로그인을 성공했습니다.")
            .data(authService.login(dto))
            .build();
  }

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
