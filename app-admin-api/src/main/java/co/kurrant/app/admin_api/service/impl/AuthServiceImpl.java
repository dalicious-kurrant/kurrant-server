package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.filter.provider.JwtTokenProvider;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.admin_api.dto.LoginRequestDto;
import co.kurrant.app.admin_api.dto.LoginResponseDto;
import co.kurrant.app.admin_api.service.AuthService;
import co.kurrant.app.client.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    arr.add(user.getRole().getAuthority());
    List<String> roleList = new ArrayList<>();
    String avartarUrl = "";
    String accessToken = String.valueOf(jwtTokenProvider.createToken("1", roleList, user.getName(), avartarUrl));


    return LoginResponseDto.builder().accessToken(accessToken).expiresIn(86400).build();
  }

}
