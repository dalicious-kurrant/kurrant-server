package co.kurrant.app.client_api.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.dalicious.client.core.filter.provider.JwtTokenProvider;
import co.kurrant.app.client_api.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  /*
  @Override
  public LoginResponseDto login(LoginRequestDto body) {

    User user = userRepository.findByEmail(body.getUsername()).orElseThrow(() -> {
      throw new ApiException(ExceptionEnum.USER_NOT_FOUND);
    });

    if (!passwordEncoder.matches(body.getPassword(), user.getPassword())) {
      throw new ApiException(ExceptionEnum.PASSWORD_DOES_NOT_MATCH);
    }

    List<String> roles = new ArrayList<>();
    roles.add(user.getRole().getAuthority());



    JwtTokenProvider.TokenResponseDto token =
        jwtTokenProvider.createToken(user.getId().toString(), roles, user.getName(), null);

    return LoginResponseDto.builder().idToken(token.getIdToken())
        .accessToken(token.getAccessToken()).refreshToken(token.getRefreshToken())
        .expiresIn(token.getExpiresIn()).tokenType("Bearer").build();


  }
 */
}