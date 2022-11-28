package co.kurrant.app.public_api.service.impl.mapper;


import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.user.entity.Apartment;
import co.dalicious.domain.user.dto.ApartmentDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ApartmentMapper extends GenericMapper<ApartmentDto, Apartment> {
    ApartmentMapper INSTANCE = Mappers.getMapper(ApartmentMapper.class);
}