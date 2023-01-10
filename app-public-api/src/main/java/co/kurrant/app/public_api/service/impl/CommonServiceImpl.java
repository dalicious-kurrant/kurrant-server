package co.kurrant.app.public_api.service.impl;

import co.kurrant.app.public_api.model.SecurityUser;
import exception.ApiException;
import exception.ExceptionEnum;
import co.dalicious.client.core.filter.provider.JwtTokenProvider;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;
import co.kurrant.app.public_api.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public User getUser(HttpServletRequest httpServletRequest) {
        // 토큰 가져오기
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        // 유효한 토큰인지 확인
        jwtTokenProvider.validateToken(token);
        // 유저 정보 가져오기
        BigInteger userId = BigInteger.valueOf(Integer.parseInt(jwtTokenProvider.getUserPk(token)));

        return userRepository.findById(userId).orElseThrow(
                () -> new ApiException(ExceptionEnum.USER_NOT_FOUND)
        );

    }

    @Override
    public BigInteger getUserId(HttpServletRequest httpServletRequest) {
        // 토큰 가져오기
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        // 유효한 토큰인지 확인
        jwtTokenProvider.validateToken(token);
        // 유저 아이디 가져오기
        return BigInteger.valueOf(Integer.parseInt(jwtTokenProvider.getUserPk(token)));
    }

    @Override
    public User getUser(SecurityUser securityUser) {
        return userRepository.findById(securityUser.getId()).orElseThrow(
                () -> new ApiException(ExceptionEnum.USER_NOT_FOUND)
        );
    }

    @Override
    public BigInteger getUserId(SecurityUser securityUser) {
        return securityUser.getId();
    }


}
