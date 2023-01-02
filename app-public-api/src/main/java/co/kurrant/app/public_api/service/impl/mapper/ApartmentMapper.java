package co.kurrant.app.public_api.service.impl.mapper;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.ApartmentResponseDto;
import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.user.dto.ApartmentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ApartmentMapper extends GenericMapper<ApartmentResponseDto, Apartment> {
    ApartmentMapper INSTANCE = Mappers.getMapper(ApartmentMapper.class);

    @Override
    @Mapping(source = "address", target = "address", qualifiedByName = "addressToString")
    ApartmentResponseDto toDto(Apartment apartment);

    @Named("addressToString")
    default String addressToString(Address address) {
        return address.addressToString();
    }
}