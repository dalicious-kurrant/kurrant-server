package co.kurrant.app.public_api.service.impl.mapper;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.application_form.dto.corporation.CorporationMealInfoResponseDto;
import co.dalicious.domain.application_form.entity.CorporationApplicationMealInfo;
import co.dalicious.domain.application_form.entity.PriceAverage;
import co.dalicious.system.util.DaysUtil;
import co.dalicious.system.util.DiningType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CorporationMealInfoResMapper extends GenericMapper<CorporationMealInfoResponseDto, CorporationApplicationMealInfo> {
    CorporationMealInfoResMapper INSTANCE = Mappers.getMapper(CorporationMealInfoResMapper.class);

    @Override
    @Mapping(source = "diningType", target = "diningType", qualifiedByName = "diningTypeToString")
    @Mapping(source = "priceAverage", target = "priceAverage", qualifiedByName = "priceAverageToString")
    @Mapping(source = "serviceDays", target = "serviceDays", qualifiedByName = "dbDataToString")
    CorporationMealInfoResponseDto toDto(CorporationApplicationMealInfo corporationApplicationMealInfo);

    @Named("diningTypeToString")
    default String diningTypeToString(DiningType diningType) {
        return diningType.getDiningType();
    }

    @Named("priceAverageToString")
    default String priceAverageToString(PriceAverage priceAverage) {
        return priceAverage.getPriceAverage();
    }

    @Named("dbDataToString")
    default String dbDataToString(String dbData) {
        return DaysUtil.serviceDaysToString(dbData);
    }
}
