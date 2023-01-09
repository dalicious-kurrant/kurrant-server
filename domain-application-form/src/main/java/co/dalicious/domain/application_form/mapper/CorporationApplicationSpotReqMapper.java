package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.application_form.dto.corporation.CorporationSpotRequestDto;
import co.dalicious.domain.application_form.entity.CorporationApplicationFormSpot;
import co.dalicious.system.util.DiningType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CorporationApplicationSpotReqMapper {
    @Mapping(source = "spotName", target = "name")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "diningTypes", target = "diningTypes", qualifiedByName = "codeToDiningType")
    CorporationApplicationFormSpot toEntity(CorporationSpotRequestDto corporationApplicationFormSpot);

    @Named("codeToDiningType")
    default List<DiningType> codeToDiningType(List<Integer> diningTypes) {
        List<DiningType> diningTypeList = new ArrayList<>();
        for(Integer diningType : diningTypes) {
            diningTypeList.add(DiningType.ofCode(diningType));
        }
        return diningTypeList;
    }
}
