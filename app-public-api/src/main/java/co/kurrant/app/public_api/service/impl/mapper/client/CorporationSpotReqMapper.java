package co.kurrant.app.public_api.service.impl.mapper.client;

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

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CorporationSpotReqMapper extends GenericMapper<CorporationSpotRequestDto, CorporationApplicationFormSpot> {
    CorporationSpotReqMapper INSTANCE = Mappers.getMapper(CorporationSpotReqMapper.class);

    @Mapping(source = "spotName", target = "name")
    @Mapping(source = "address", target = "address", qualifiedByName = "addressDtoToEntity")
    @Mapping(source = "diningTypes", target = "diningTypes", qualifiedByName = "codeToDiningType")
    @Override
    CorporationApplicationFormSpot toEntity(CorporationSpotRequestDto dto);

    @Mapping(source = "diningTypes", target = "diningTypes", qualifiedByName = "diningTypeToList")
    @Override
    CorporationSpotRequestDto toDto(CorporationApplicationFormSpot spot);

    default List<DiningType> map(List<Integer> diningTypes) {
        List<DiningType> diningTypeList = new ArrayList<>();
        for(Integer diningType : diningTypes) {
            diningTypeList.add(DiningType.ofCode(diningType));
        }
        return diningTypeList;
    }

    @Named("addressDtoToEntity")
    default Address addressDtoToEntity(CreateAddressRequestDto dto) {
        return Address.builder()
                .createAddressRequestDto(dto)
                .build();
    }

    @Named("codeToDiningType")
    default List<DiningType> codeToDiningType(List<Integer> diningTypes) {
        List<DiningType> diningTypeList = new ArrayList<>();
        for(Integer diningType : diningTypes) {
            diningTypeList.add(DiningType.ofCode(diningType));
        }
        return diningTypeList;
    }

    @Named("diningTypeToList")
    default List<Integer> diningTypeToList(List<DiningType> diningTypes) {
        List<Integer> diningTypeList = new ArrayList<>();
        for(DiningType diningType : diningTypes) {
            diningTypeList.add(diningType.getCode());
        }
        return diningTypeList;
    }
}
