package co.kurrant.app.admin_api.util;

import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.MakersRepository;
import co.kurrant.app.admin_api.model.Admin;
import co.kurrant.app.admin_api.model.SecurityUser;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
@RequiredArgsConstructor
public class UserUtil {
    // SecurityUser를 통해서 유저 객체 정보를 가져온다.
    public Admin getAdmin(SecurityUser securityUser) {
        if(!securityUser.getUsername().equals("admin")) throw new ApiException(ExceptionEnum.USER_NOT_FOUND);

        return new Admin(securityUser.getUsername(), securityUser.getPassword());
    }

    public static SecurityUser securityUser(Authentication authentication) {
        if(authentication == null) {
            throw new ApiException(ExceptionEnum.ACCESS_TOKEN_ERROR);
        }
        return (SecurityUser) authentication.getPrincipal();
    }

}