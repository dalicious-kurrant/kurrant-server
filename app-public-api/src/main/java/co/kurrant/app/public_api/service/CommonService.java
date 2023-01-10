package co.kurrant.app.public_api.service;


import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.model.SecurityUser;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;

public interface CommonService {
    // Access 토큰을 통해서 유저 객체 정보를 가져온다.
    User getUser(HttpServletRequest httpServletRequest);
    // Access 토큰을 통해서 유저의 id를 가져온다.
    BigInteger getUserId(HttpServletRequest httpServletRequest);
    // SecurityUser를 통해서 유저 객체 정보를 가져온다.
    User getUser(SecurityUser securityUser);
    // SecurityUser를 통해서 유저의 id를 가져온다.
    BigInteger getUserId(SecurityUser securityUser);
}
