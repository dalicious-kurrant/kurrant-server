package co.kurrant.app.public_api.service;

import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;
import co.kurrant.app.public_api.model.SecurityUser;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
@RequiredArgsConstructor
public class UserUtil {
    private final UserRepository userRepository;
    // SecurityUser를 통해서 유저 객체 정보를 가져온다.
    public User getUser(SecurityUser securityUser) {
        return userRepository.findById(securityUser.getId()).orElseThrow(
                () -> new ApiException(ExceptionEnum.USER_NOT_FOUND)
        );
    }
    // SecurityUser를 통해서 유저의 id를 가져온다.
    public BigInteger getUserId(SecurityUser securityUser) {
        return securityUser.getId();
    }

    public static SecurityUser securityUser(Authentication authentication) {
        if(authentication == null) {
            throw new ApiException(ExceptionEnum.ACCESS_TOKEN_ERROR);
        }
        return (SecurityUser) authentication.getPrincipal();
    }

}
