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
public interface CorporationMealInfoReqMapper extends GenericMapper<CorporationMealInfoRequestDto, CorporationMealInfo> {
    CorporationMealInfoReqMapper INSTANCE = Mappers.getMapper(CorporationMealInfoReqMapper.class);

    @Override
    @Mapping(source = "diningType", target = "diningType", qualifiedByName = "codeToDiningType")
    @Mapping(source = "priceAverage", target = "priceAverage", qualifiedByName = "codeToPriceAverage")
    CorporationMealInfo toEntity(CorporationMealInfoRequestDto dto);

    @Named("codeToDiningType")
    default DiningType CodeToDiningType(Integer diningType) {
        return DiningType.ofCode(diningType);
    }

    @Named("codeToPriceAverage")
    default PriceAverage CodeToPriceAverage(Integer priceAverage) {
        return PriceAverage.ofCode(priceAverage);
    }
}
