package co.kurrant.app.public_api.mapper.client;

import co.dalicious.domain.application_form.dto.corporation.CorporationMealInfoRequestDto;
import co.dalicious.domain.application_form.entity.CorporationApplicationMealInfo;
import co.dalicious.domain.application_form.entity.enums.PriceAverage;
import co.dalicious.system.util.DaysUtil;
import co.dalicious.system.util.DiningType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CorporationMealInfoReqMapper{
    @Mapping(source = "diningType", target = "diningType", qualifiedByName = "codeToDiningType")
    @Mapping(source = "priceAverage", target = "priceAverage", qualifiedByName = "codeToPriceAverage")
    @Mapping(source = "serviceDays", target = "serviceDays", qualifiedByName = "listToString")
    CorporationApplicationMealInfo toEntity(CorporationMealInfoRequestDto dto);


    @Named("codeToDiningType")
    default DiningType CodeToDiningType(Integer diningType) {
        return DiningType.ofCode(diningType);
    }

    @Named("codeToPriceAverage")
    default PriceAverage CodeToPriceAverage(Integer priceAverage) {
        return PriceAverage.ofCode(priceAverage);
    }

    @Named("listToString")
    default String listToString(List<Integer> days) {
        return DaysUtil.serviceDaysToDbData(days);
    }

    @Named("stringToList")
    default List<Integer> stringToList(String days) {
        String[] daysList = days.split(", ");
        List<Integer> integerList = new ArrayList<>();
        for(String day : daysList) {
            integerList.add(Integer.parseInt(days));
        }
        return integerList;
    }
}
