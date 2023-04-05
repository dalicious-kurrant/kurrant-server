package co.dalicious.domain.paycheck.mapper;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.entity.CorporationPaycheck;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import co.dalicious.domain.paycheck.entity.enums.PaycheckStatus;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", imports = {DateUtils.class, PaycheckStatus.class})
public interface CorporationPaycheckMapper {
    @Mapping(source = "corporation", target = "corporation")
    @Mapping(target = "yearMonth", expression = "java(DateUtils.toYearMonth(paycheckDto.getYear(), paycheckDto.getMonth()))")
    @Mapping(target = "paycheckStatus", expression = "java(PaycheckStatus.ofCode(paycheckDto.getPaycheckStatus()))")
    @Mapping(source = "excelFile", target = "excelFile")
    @Mapping(source = "pdfFile", target = "pdfFile")
    @Mapping(source = "paycheckDto.managerName", target = "managerName")
    @Mapping(source = "paycheckDto.phone", target = "phone")
    CorporationPaycheck toEntity(PaycheckDto.CorporationRequest paycheckDto, Corporation corporation, Image excelFile, Image pdfFile);

    @Mapping(target = "year", expression = "java(corporationPaycheck.getYearMonth().getYear())")
    @Mapping(target = "month", expression = "java(corporationPaycheck.getYearMonth().getMonthValue())")
    @Mapping(source = "corporation.name", target = "corporationName")
    @Mapping(source = "paycheckStatus.paycheckStatus", target = "paycheckStatus")
    @Mapping(source = "excelFile.location", target = "excelFile")
    @Mapping(source = "pdfFile.location", target = "pdfFile")
    PaycheckDto.CorporationResponse toDto(CorporationPaycheck corporationPaycheck);

    default List<PaycheckDto.CorporationResponse> toDtos(List<CorporationPaycheck> corporationPaycheck) {
        return corporationPaycheck.stream()
                .map(this::toDto)
                .toList();
    }
}
