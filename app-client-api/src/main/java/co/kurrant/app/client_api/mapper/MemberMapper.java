package co.kurrant.app.client_api.mapper;

import co.dalicious.domain.client.entity.Employee;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.Role;
import co.kurrant.app.client_api.dto.MemberListResponseDto;
import co.kurrant.app.client_api.dto.MemberWaitingListResponseDto;
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


    @Mapping(source = "employee.phone", target = "phone")
    @Mapping(source = "employee.name", target = "name")
    @Mapping(source = "employee.email", target = "email")
    @Mapping(source = "employee.id", target = "id")
    MemberWaitingListResponseDto toMemberWaitingListDto(Employee employee);

    @Named("userType")
    default String userType(Role role){
        String result = "일반 사용자";
        if (role.getAuthority().equals("ROLE_ADMIN")){
            result = "관리자";
        }

    return result;
    }

}
