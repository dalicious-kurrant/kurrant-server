package co.kurrant.app.client_api.service;

import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.domain.order.dto.GroupDto;
import co.kurrant.app.client_api.model.SecurityUser;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.util.List;

public interface GroupService {
    GroupListDto.GroupInfoList getGroupInfo(SecurityUser securityUser);

    List<GroupDto.Spot> getSpots(BigInteger groupId, SecurityUser securityUser);
    void postManagerInformation(SecurityUser securityUser, String information, String value);
}
