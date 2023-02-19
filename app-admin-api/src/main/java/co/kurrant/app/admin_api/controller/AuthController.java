package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.admin_api.dto.user.LoginRequestDto;
import co.kurrant.app.admin_api.service.AuthService;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "1. Auth")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/auth")
@RestController
@CrossOrigin(origins="*", allowedHeaders = "*")
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
}
