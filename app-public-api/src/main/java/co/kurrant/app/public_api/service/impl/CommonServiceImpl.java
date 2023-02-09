package co.kurrant.app.public_api.service.impl;

import java.math.BigInteger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import co.dalicious.client.core.filter.provider.JwtTokenProvider;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.DomainUserRepository;
import co.kurrant.app.public_api.service.CommonService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {
  private final JwtTokenProvider jwtTokenProvider;
  private final DomainUserRepository userRepository;

  @Override
  public User getUser(HttpServletRequest httpServletRequest) {
    // 토큰 가져오기
    String token = jwtTokenProvider.resolveToken(httpServletRequest);
    // 유효한 토큰인지 확인
    jwtTokenProvider.validateToken(token);
    // 유저 정보 가져오기
    BigInteger userId = BigInteger.valueOf(Integer.parseInt(jwtTokenProvider.getUserPk(token)));

    return userRepository.findById(userId)
        .orElseThrow(() -> new ApiException(ExceptionEnum.USER_NOT_FOUND));

  }
}
