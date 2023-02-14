package co.kurrant.app.client.mapper;

import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.Role;
import co.kurrant.app.client.dto.MemberListResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(source = "user.id", target="id")
    @Mapping(source = "user.email", target="userId")
    @Mapping(source = "user.password", target="password")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.role", target = "userType", qualifiedByName = "userType")
    @Mapping(source = "user.phone", target = "phone")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "groupName", target = "groupName")
    @Mapping(source = "user.point", target = "point")
    @Mapping(source = "user.gourmetType", target = "gourmetType")
    @Mapping(source = "user.isMembership", target = "isMembership")
    MemberListResponseDto toMemberListDto(User user, String groupName);

    @Named("userType")
    default String userType(Role role){
        return role.getAuthority();
    }

}
