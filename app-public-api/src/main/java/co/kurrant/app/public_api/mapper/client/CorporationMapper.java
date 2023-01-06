package co.kurrant.app.public_api.mapper.client;


import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.user.dto.CorporationDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CorporationMapper extends GenericMapper<CorporationDto, Corporation> {
    CorporationMapper INSTANCE = Mappers.getMapper(CorporationMapper.class);
}