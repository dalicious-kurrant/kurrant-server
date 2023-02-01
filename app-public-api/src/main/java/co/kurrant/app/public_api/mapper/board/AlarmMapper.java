package co.kurrant.app.public_api.mapper.board;

import co.dalicious.domain.board.entity.Alarm;
import co.dalicious.domain.board.entity.enums.AlarmBoardType;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.board.AlarmResponseDto;
import jdk.jfr.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface AlarmMapper{

    @Mapping(source="alarm.user", target = "userId", qualifiedByName = "setUser")
    @Mapping(source="alarm.type", target = "type", qualifiedByName = "setAlarmType")
    @Mapping(source = "alarm.created", target = "created", qualifiedByName = "created")
    AlarmResponseDto toDto(Alarm alarm);

    @Named("setAlarmType")
    default String setAlarmType(AlarmBoardType alarmBoardType){
        return alarmBoardType.getAlarmType();
    }

    @Named("setUser")
    default BigInteger setUser(User user){
        return user.getId();
    }

    @Named("created")
    default String created(LocalDateTime created){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(created);
    }
}
