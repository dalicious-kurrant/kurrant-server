package co.kurrant.app.client_api.service;

import co.dalicious.domain.client.dto.GroupListDto;
import co.kurrant.app.client_api.model.SecurityUser;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;

public interface GroupService {
    GroupListDto.GroupInfoList getGroupInfo(SecurityUser securityUser);
}
