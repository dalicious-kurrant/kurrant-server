package co.dalicious.domain.user.mapper;

import co.dalicious.domain.user.entity.Founders;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FoundersMapper {
    @Mapping(source = "user", target = "user")
    @Mapping(source = "membership", target = "membership")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(source = "foundersNumber", target = "foundersNumber")
    Founders toEntity(User user, Membership membership, Integer foundersNumber);
}
