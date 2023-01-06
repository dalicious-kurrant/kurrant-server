package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.corporation.CorporationSpotResponseDto;
import co.dalicious.domain.application_form.entity.CorporationApplicationFormSpot;
import co.dalicious.system.util.DiningType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CorporationSpotResMapper {
    CorporationSpotResMapper INSTANCE = Mappers.getMapper(CorporationSpotResMapper.class);

    @Mapping(source = "address", target = "address", qualifiedByName = "addressToString")
    @Mapping(source = "diningTypes", target = "diningTypes", qualifiedByName = "diningTypeToString")
    @Mapping(source = "name", target = "spotName")
    CorporationSpotResponseDto toDto(CorporationApplicationFormSpot applicationFormSpot);

    @Mapping(target = "address", ignore = true)
    CorporationApplicationFormSpot toEntity(CorporationSpotResponseDto dto);

    @Named("addressToString")
    default String addressToString(Address address) {
        return address.addressToString();
    }


    @Named("diningTypeToString")
    default List<String> diningTypeToString(List<DiningType> diningTypes) {
        List<String> diningTypeList = new ArrayList<>();
        for(DiningType diningType : diningTypes) {
            diningTypeList.add(diningType.getDiningType());
        }
        return diningTypeList;
    }
}
