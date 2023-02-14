package co.kurrant.app.makers_api.util;

import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.MakersRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;
import co.kurrant.app.makers_api.model.SecurityUser;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
@RequiredArgsConstructor
public class UserUtil {
    private final MakersRepository makersRepository;
    // SecurityUser를 통해서 유저 객체 정보를 가져온다.
    public Makers getMakers(SecurityUser securityUser) {
        return makersRepository.findById(securityUser.getId()).orElseThrow(
                () -> new ApiException(ExceptionEnum.USER_NOT_FOUND)
        );
    }
    // SecurityUser를 통해서 유저의 id를 가져온다.
    public BigInteger getMakersId(SecurityUser securityUser) {
        return securityUser.getId();
    }

    public static SecurityUser securityUser(Authentication authentication) {
        if(authentication == null) {
            throw new ApiException(ExceptionEnum.ACCESS_TOKEN_ERROR);
        }
        return (SecurityUser) authentication.getPrincipal();
    }

}
