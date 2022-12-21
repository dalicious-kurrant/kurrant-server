package co.kurrant.app.public_api.service.impl.mapper;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.user.UserHomeResponseDto;
import co.kurrant.app.public_api.dto.user.UserInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserHomeInfoMapper extends GenericMapper<UserHomeResponseDto, User> {
    UserHomeInfoMapper INSTANCE = Mappers.getMapper(UserHomeInfoMapper.class);
}
