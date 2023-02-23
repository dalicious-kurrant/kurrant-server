package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.enums.Role;
import co.dalicious.domain.user.validator.UserValidator;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.user.SaveUserListRequestDto;
import co.kurrant.app.admin_api.dto.user.UserInfoResponseDto;
import jdk.jfr.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.sql.Timestamp;
import java.util.List;

@Mapper(componentModel = "spring", imports = {DateUtils.class, UserValidator.class})
public interface UserMapper {

    @Mapping(source = "user.updatedDateTime", target = "userUpdatedDateTime", qualifiedByName = "TimeFormat")
    @Mapping(source = "user.createdDateTime", target = "userCreatedDateTime", qualifiedByName = "TimeFormat")
    @Mapping(source = "user.recentLoginDateTime", target = "recentLoginDateTime", qualifiedByName = "TimeFormat")
    @Mapping(source = "user.orderAlarm", target = "userOrderAlarm")
    @Mapping(source = "user.marketingAgreedDateTime", target = "userEmailAgreedDateTime")
    @Mapping(source = "user.marketingAgree", target = "userEmailAgreed")
    @Mapping(source = "user.isMembership", target = "isMembership")
    @Mapping(source = "user.gourmetType",target = "gourmetType")
    @Mapping(source = "user.point",target = "point")
    @Mapping(source = "user.groups", target = "groupName", qualifiedByName = "getGroupNameAll")
    @Mapping(source = "user.email",target = "email")
    @Mapping(source = "user.phone", target = "phone")
    @Mapping(source = "user.role", target = "role")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.password", target = "password")
    UserInfoResponseDto toDto(User user);

    @Named("getGroupNameAll")
    default String getGroupNameAll(List<UserGroup> groups){

        if (groups.size() == 0) return "없음";

        if (groups.size() != 1){
            StringBuilder resultName = null;
            for (UserGroup userGroup : groups){
                resultName = new StringBuilder(userGroup.getGroup().getName() + ", ");
            }
            return resultName.substring(0, resultName.length()-2);
        }
        return groups.get(0).getGroup().getName();
    }

    @Named("TimeFormat")
    default String TimeFormat(Timestamp time){
        return DateUtils.format(time, "yyyy-MM-dd, HH:mm:ss");
    }

    @Mapping(source = "password", target = "password")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "saveUser.phone", target = "phone")
    @Mapping(source = "saveUser.email", target = "email")
    @Mapping(source = "saveUser.name", target = "name")
    @Mapping(source = "saveUser.userId", target = "id")
    User toEntity(SaveUserListRequestDto saveUser, String password, Role role);

}