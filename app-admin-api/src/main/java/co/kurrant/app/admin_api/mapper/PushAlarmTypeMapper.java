package co.kurrant.app.admin_api.mapper;

import co.dalicious.client.alarm.dto.HandlePushAlarmDto;
import co.dalicious.client.alarm.entity.enums.HandlePushAlarmType;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PushAlarmTypeMapper {

    default HandlePushAlarmDto.HandlePushAlarmType toHandlePushAlarmType(HandlePushAlarmType type) {
        HandlePushAlarmDto.HandlePushAlarmType handlePushAlarmType = new HandlePushAlarmDto.HandlePushAlarmType();

        handlePushAlarmType.setCode(type.getCode());
        handlePushAlarmType.setType(type.getType());

        return handlePushAlarmType;
    }

    default HandlePushAlarmDto.HandlePushAlarmGroup toHandlePushAlarmGroup(Group group) {
        HandlePushAlarmDto.HandlePushAlarmGroup handlePushAlarmGroup = new HandlePushAlarmDto.HandlePushAlarmGroup();

        handlePushAlarmGroup.setGroupId(group.getId());
        handlePushAlarmGroup.setGroupName(group.getName());

        return handlePushAlarmGroup;
    }

    default HandlePushAlarmDto.HandlePushAlarmSpot toHandlePushAlarmSpot(Spot spot) {
        HandlePushAlarmDto.HandlePushAlarmSpot handlePushAlarmSpot = new HandlePushAlarmDto.HandlePushAlarmSpot();

        handlePushAlarmSpot.setSpotId(spot.getId());
        handlePushAlarmSpot.setSpotName(spot.getName());

        return handlePushAlarmSpot;
    }

    default HandlePushAlarmDto.HandlePushAlarmUser toHandlePushAlarmUser(User user) {
        HandlePushAlarmDto.HandlePushAlarmUser handlePushAlarmUser = new HandlePushAlarmDto.HandlePushAlarmUser();

        handlePushAlarmUser.setUserid(user.getId());
        handlePushAlarmUser.setName(user.getName());
        handlePushAlarmUser.setEmail(user.getEmail());

        return handlePushAlarmUser;
    }
}
