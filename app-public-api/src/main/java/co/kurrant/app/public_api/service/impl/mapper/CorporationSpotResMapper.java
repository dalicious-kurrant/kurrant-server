package co.kurrant.app.public_api.service.impl.mapper;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.corporation.CorporationSpotResponseDto;
import co.dalicious.domain.application_form.entity.CorporationApplicationFormSpot;
import co.dalicious.system.util.DiningType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CorporationSpotResMapper extends GenericMapper<CorporationSpotResponseDto, CorporationApplicationFormSpot> {
    CorporationSpotResMapper INSTANCE = Mappers.getMapper(CorporationSpotResMapper.class);

    @Override
    @Mapping(source = "address", target = "address", qualifiedByName = "addressToString")
    @Mapping(source = "diningType", target = "diningType", qualifiedByName = "diningTypeToString")
    CorporationSpotResponseDto toDto(CorporationApplicationFormSpot applicationFormSpot);

    @Named("addressToString")
    default String addressToString(Address address) {
        return address.getAddress1() + " " + address.getAddress2();
    }


    @Named("diningTypeToString")
    default String diningTypeToString(DiningType diningType) {
        return diningType.getDiningType();
    }
}
