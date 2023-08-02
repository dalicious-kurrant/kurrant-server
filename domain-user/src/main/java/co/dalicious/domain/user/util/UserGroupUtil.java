package co.dalicious.domain.user.util;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import exception.ApiException;
import exception.ExceptionEnum;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserGroupUtil {
    public static void isUserIncludedInGroup(User user, Group group) {
        List<UserGroup> userGroups = user.getActiveUserGroups();
        userGroups.stream().filter(v -> v.getGroup().equals(group))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.UNAUTHORIZED));
    }
}
