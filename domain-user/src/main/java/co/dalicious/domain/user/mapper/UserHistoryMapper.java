package co.dalicious.domain.user.mapper;

import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigInteger;

@Mapper(componentModel = "spring")
public interface UserHistoryMapper {

    @Mapping(source = "groupId", target = "groupId")
    @Mapping(source = "deleteUser.phone", target = "email")
    @Mapping(source = "deleteUser.email", target = "phone")
    @Mapping(source = "deleteUser.name", target = "name")
    @Mapping(source = "deleteUser.id", target = "userId")
    UserHistory toEntity(User deleteUser, BigInteger groupId);


}
