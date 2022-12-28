package co.kurrant.app.public_api.service.impl.mapper;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.application_form.dto.corporation.CorporationMealInfoRequestDto;
import co.dalicious.domain.application_form.entity.CorporationMealInfo;
import co.dalicious.domain.application_form.entity.PriceAverage;
import co.dalicious.system.util.DiningType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CorporationMealInfoMapper extends GenericMapper<CorporationMealInfoRequestDto, CorporationMealInfo> {
    CorporationMealInfoMapper INSTANCE = Mappers.getMapper(CorporationMealInfoMapper.class);

    @Override
    @Mapping(source = "diningType", target = "diningType", qualifiedByName = "CodeToDiningType")
    @Mapping(source = "priceAverage", target = "priceAverage", qualifiedByName = "CodeToPriceAverage")
    CorporationMealInfo toEntity(CorporationMealInfoRequestDto dto);

    @Named("CodeToDiningType")
    default DiningType CodeToDiningType(Integer diningType) {
        return DiningType.ofCode(diningType);
    }

    @Named("CodeToPriceAverage")
    default PriceAverage CodeToPriceAverage(Integer priceAverage) {
        return PriceAverage.ofCode(priceAverage);
    }
}
