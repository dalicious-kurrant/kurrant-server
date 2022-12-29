package co.kurrant.app.public_api.service.impl.mapper;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.corporation.CorporationSpotRequestDto;
import co.dalicious.domain.application_form.entity.CorporationApplicationFormSpot;
import co.dalicious.system.util.DiningType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CorporationSpotReqMapper extends GenericMapper<CorporationSpotRequestDto, CorporationApplicationFormSpot> {
    CorporationSpotReqMapper INSTANCE = Mappers.getMapper(CorporationSpotReqMapper.class);

    @Mapping(source = "spotName", target = "name")
    @Mapping(source = "address", target = "address", qualifiedByName = "addressDtoToEntity")
    @Mapping(source = "diningType", target = "diningType", qualifiedByName = "codeToDiningType")
    @Override
    CorporationApplicationFormSpot toEntity(CorporationSpotRequestDto dto);

    @Named("addressDtoToEntity")
    default Address addressDtoToEntity(CreateAddressRequestDto dto) {
        return Address.builder()
                .createAddressRequestDto(dto)
                .build();
    }

    @Named("codeToDiningType")
    default DiningType codeToDiningType(Integer diningType) {
        return DiningType.ofCode(diningType);
    }
}
