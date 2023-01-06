package co.kurrant.app.public_api.mapper.client;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.ApartmentResponseDto;
import co.dalicious.domain.client.entity.Apartment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ApartmentListMapper extends GenericMapper<ApartmentResponseDto, Apartment> {
    ApartmentListMapper INSTANCE = Mappers.getMapper(ApartmentListMapper.class);

    @Override
    @Mapping(source = "address", target = "address", qualifiedByName = "addressToString")
    ApartmentResponseDto toDto(Apartment apartment);

    @Mapping(source = "address", target = "address", ignore = true)
    Apartment toEntity(ApartmentResponseDto apartment);

    @Named("addressToString")
    default String addressToString(Address address) {
        return address.addressToString();
    }
}