package co.kurrant.app.client_api.service.impl;

import co.dalicious.client.core.dto.request.LoginTokenDto;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.repository.QCorporationRepository;
import co.dalicious.domain.user.entity.enums.Role;
import co.kurrant.app.client_api.dto.LoginRequestDto;
import co.kurrant.app.client_api.dto.LoginResponseDto;
import exception.ApiException;
import exception.ExceptionEnum;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.dalicious.client.core.filter.provider.JwtTokenProvider;
import co.kurrant.app.client_api.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final QCorporationRepository qCorporationRepository;

  @Override
  public LoginResponseDto login(LoginRequestDto dto) {

    //id check
    if(dto.getCode() == null) {
      throw new ApiException(ExceptionEnum.NOT_INPUT_USERNAME);
    }
    Corporation corporation = qCorporationRepository.findEntityByCode(dto.getCode());

    if (corporation == null) {
      throw new ApiException(ExceptionEnum.NOT_MATCHED_USERNAME);
    }

    //password check
    if(dto.getPassword() == null) {
      throw new ApiException(ExceptionEnum.NOT_INPUT_PASSWORD);
    } else if(!dto.getPassword().equals("12345678")) {
      throw new ApiException(ExceptionEnum.PASSWORD_DOES_NOT_MATCH);
    }


    return getLoginAccessToken(corporation);
  }

  public LoginResponseDto getLoginAccessToken(Corporation corporation) {
    // 토큰에 권한 넣기
    List<String> authorities = new ArrayList<>();

    authorities.add(Role.ADMIN.getAuthority());
    LoginTokenDto loginResponseDto = jwtTokenProvider.createToken(corporation.getCode(), authorities);

    return LoginResponseDto.create(loginResponseDto, corporation);
  }


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
