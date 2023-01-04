package co.kurrant.app.public_api.service.impl.mapper.user;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.user.UserPersonalInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserPersonalInfoMapper extends GenericMapper<UserPersonalInfoDto, User> {
    UserPersonalInfoMapper INSTANCE = Mappers.getMapper(UserPersonalInfoMapper.class);

    @Override
    @Mapping(target = "avatar", source = "avatar.location")
    @Mapping(target = "gourmetType", source = "gourmetType.gourmetType")
    UserPersonalInfoDto toDto(User user);
}
