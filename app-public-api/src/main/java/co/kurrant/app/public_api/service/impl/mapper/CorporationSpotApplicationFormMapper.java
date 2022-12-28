package co.kurrant.app.public_api.service.impl.mapper;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.corporation.CorporationSpotApplicationFormDto;
import co.dalicious.domain.application_form.entity.CorporationApplicationFormSpot;
import co.dalicious.system.util.DiningType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CorporationSpotApplicationFormMapper extends GenericMapper<CorporationSpotApplicationFormDto, CorporationApplicationFormSpot> {
    CorporationSpotApplicationFormMapper INSTANCE = Mappers.getMapper(CorporationSpotApplicationFormMapper.class);

    @Mapping(source = "spotName", target = "name")
    @Mapping(source = "address", target = "address", qualifiedByName = "addressDtoToEntity")
    @Mapping(source = "diningType", target = "diningType", qualifiedByName = "CodeToDiningType")
    @Override
    CorporationApplicationFormSpot toEntity(CorporationSpotApplicationFormDto dto);

    @Named("addressDtoToEntity")
    default Address addressDtoToEntity(CreateAddressRequestDto dto) {
        return Address.builder()
                .createAddressRequestDto(dto)
                .build();
    }

    @Named("CodeToDiningType")
    default DiningType diningTypeDtoToEntity(Integer diningType) {
        return DiningType.ofCode(diningType);
    }
}
