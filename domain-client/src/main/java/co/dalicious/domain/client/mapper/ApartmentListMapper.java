package co.dalicious.domain.client.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.ApartmentResponseDto;
import co.dalicious.domain.client.entity.Apartment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ApartmentListMapper {

    @Mapping(source = "address", target = "address", qualifiedByName = "addressToString")
    ApartmentResponseDto toDto(Apartment apartment);

    @Named("addressToString")
    default String addressToString(Address address) {
        return address.addressToString();
    }
}
