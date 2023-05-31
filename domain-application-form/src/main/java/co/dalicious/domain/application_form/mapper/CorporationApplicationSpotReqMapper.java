package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.corporation.CorporationSpotRequestDto;
import co.dalicious.domain.application_form.entity.CorporationApplicationFormSpot;
import co.dalicious.system.enums.DiningType;
import org.locationtech.jts.io.ParseException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CorporationApplicationSpotReqMapper {
    @Mapping(source = "spotName", target = "name")
    @Mapping(source = "address", target = "address", qualifiedByName = "getAddress")
    @Mapping(source = "diningTypes", target = "diningTypes", qualifiedByName = "codeToDiningType")
    CorporationApplicationFormSpot toEntity(CorporationSpotRequestDto corporationApplicationFormSpot) throws ParseException;

    @Named("codeToDiningType")
    default List<DiningType> codeToDiningType(List<Integer> diningTypes) {
        List<DiningType> diningTypeList = new ArrayList<>();
        for(Integer diningType : diningTypes) {
            diningTypeList.add(DiningType.ofCode(diningType));
        }
        return diningTypeList;
    }

    @Named("getAddress")
    default Address getAddress(CreateAddressRequestDto createAddressRequestDto) throws org.locationtech.jts.io.ParseException {
        // FIXME: 위도 경도 추가?
            return new Address(createAddressRequestDto.getZipCode(),
                    createAddressRequestDto.getAddress1(),
                    createAddressRequestDto.getAddress2(),
                    null);

    }


}
