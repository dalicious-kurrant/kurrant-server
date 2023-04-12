package co.dalicious.domain.paycheck.mapper;

import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import co.dalicious.domain.paycheck.entity.PaycheckDailyFood;
import co.dalicious.domain.paycheck.entity.enums.PaycheckStatus;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.YearMonth;
import java.util.List;

@Mapper(componentModel = "spring", imports = {DateUtils.class, PaycheckStatus.class})
public interface MakersPaycheckMapper {

    @Mapping(source = "makers", target = "makers")
    @Mapping(target = "yearMonth", expression = "java(DateUtils.toYearMonth(paycheckDto.getYear(), paycheckDto.getMonth()))")
    @Mapping(target = "paycheckStatus", expression = "java(PaycheckStatus.ofCode(paycheckDto.getPaycheckStatus()))")
    @Mapping(source = "excelFile", target = "excelFile")
    @Mapping(source = "pdfFile", target = "pdfFile")
    MakersPaycheck toEntity(PaycheckDto.MakersRequest paycheckDto, Makers makers, Image excelFile, Image pdfFile);

    @Mapping(source = "makers", target = "makers")
    @Mapping(target = "yearMonth", expression = "java(YearMonth.now())")
    @Mapping(target = "paycheckStatus", constant = "REGISTER")
    @Mapping(source = "excelFile", target = "excelFile")
    @Mapping(source = "pdfFile", target = "pdfFile")
    @Mapping(source = "paycheckDailyFoods", target = "paycheckDailyFoods")
    MakersPaycheck toInitiateEntity(Makers makers, Image excelFile, Image pdfFile, List<PaycheckDailyFood> paycheckDailyFoods);


    @Mapping(target = "year", expression = "java(makersPaycheck.getYearMonth().getYear())")
    @Mapping(target = "month", expression = "java(makersPaycheck.getYearMonth().getMonthValue())")
    @Mapping(source = "makers.name", target = "makersName")
    @Mapping(source = "makers.depositHolder", target = "accountHolder")
    @Mapping(source = "makers.bank", target = "nameOfBank")
    @Mapping(source = "makers.accountNumber", target = "accountNumber")
    @Mapping(source = "paycheckStatus.paycheckStatus", target = "paycheckStatus")
    @Mapping(source = "excelFile.location", target = "excelFile")
    @Mapping(source = "pdfFile.location", target = "pdfFile")
    PaycheckDto.MakersResponse toDto(MakersPaycheck makersPaycheck);

    @Named("getNowYearMonth")
    default YearMonth getNowYearMonth() {
        return YearMonth.now();
    }

    default List<PaycheckDto.MakersResponse> toDtos(List<MakersPaycheck> makersPaychecks) {
        return makersPaychecks.stream()
                .map(this::toDto)
                .toList();
    }
}
