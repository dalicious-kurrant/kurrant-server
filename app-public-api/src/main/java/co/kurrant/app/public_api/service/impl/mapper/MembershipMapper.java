package co.kurrant.app.public_api.service.impl.mapper;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.user.entity.Membership;
import co.kurrant.app.public_api.dto.user.MembershipDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MembershipMapper extends GenericMapper<MembershipDto, Membership> {
    MembershipMapper INSTANCE = Mappers.getMapper(MembershipMapper.class);
}
