package co.kurrant.app.public_api.mapper.board;

import co.dalicious.domain.board.entity.Alarm;
import co.dalicious.domain.board.entity.enums.AlarmBoardType;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.public_api.dto.board.AlarmResponseDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface AlarmMapper{

    @Mapping(source= "user", target = "userId", qualifiedByName = "setUser")
    @Mapping(source= "type", target = "type", qualifiedByName = "setAlarmType")
    @Mapping(source = "createdDateTime", target = "created", qualifiedByName = "created")
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
    default String created(Timestamp createdDateTime){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(createdDateTime.toLocalDateTime());
    }
}
