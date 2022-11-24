package co.kurrant.app.admin_api.service.impl;

import java.util.ArrayList;
import java.util.List;

import co.kurrant.app.admin_api.dto.LoginRequestDto;
import co.kurrant.app.admin_api.dto.LoginResponseDto;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.dalicious.client.core.filter.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import co.kurrant.app.admin_api.service.AuthService;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Exception.class)
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public LoginResponseDto login(LoginRequestDto dto) {
    User user = userRepository.findByEmail(dto.getUsername()).orElseThrow(() -> {
      return new UsernameNotFoundException("");
    });

    // if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
    // throw new ApiException(ExceptionEnum.PASSWORD_DOES_NOT_MATCH);
    // }

    List<String> arr = new ArrayList<String>();
    String accessToken = jwtTokenProvider.createToken(user.getId().toString(), arr);
    // user.getUserRoles().stream().map(userRoles -> userRoles.getRole())
    // .map(role -> role.getName()).collect(Collectors.toList()));

    return LoginResponseDto.builder().accessToken(accessToken).expiresIn(86400).build();
  }

}
