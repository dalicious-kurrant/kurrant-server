package co.kurrant.app.client_api.util;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.repository.CorporationRepository;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.kurrant.app.client_api.model.SecurityUser;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
@RequiredArgsConstructor
public class UserUtil {
    private final CorporationRepository corporationRepository;

    public Corporation getCorporation(SecurityUser securityUser) {
        return corporationRepository.findById(securityUser.getId())
                .orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));
    }

    // SecurityUser를 통해서 유저 객체 정보를 가져온다.
    public BigInteger getGroupId(SecurityUser securityUser) {
        return securityUser.getId();

    }

    public static SecurityUser securityUser(Authentication authentication) {
        if(authentication == null) {
            throw new ApiException(ExceptionEnum.ACCESS_TOKEN_ERROR);
        }
        return (SecurityUser) authentication.getPrincipal();
    }

}
