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

    default HandlePushAlarmDto.HandlePushAlarm toHandlePushAlarmByType(Group group, Spot spot, User user) {
        HandlePushAlarmDto.HandlePushAlarm handlePushAlarm = new HandlePushAlarmDto.HandlePushAlarm();

        if(group != null) {
            handlePushAlarm.setId(group.getId());
            handlePushAlarm.setName(group.getName());
        }
        else if(spot != null) {
            handlePushAlarm.setId(spot.getId());
            handlePushAlarm.setName(spot.getName());
        }
        else if(user != null) {
            handlePushAlarm.setId(user.getId());
            handlePushAlarm.setName(user.getName());
            handlePushAlarm.setEmail(user.getEmail());
        }
        return handlePushAlarm;
    }
}
