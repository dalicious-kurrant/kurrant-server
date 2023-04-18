package co.dalicious.domain.paycheck.mapper;

import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.dto.TransactionInfoDefault;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import co.dalicious.domain.paycheck.entity.PaycheckAdd;
import co.dalicious.domain.paycheck.entity.PaycheckDailyFood;
import co.dalicious.domain.paycheck.entity.enums.PaycheckStatus;
import co.dalicious.system.util.DateUtils;
import org.apache.poi.hpsf.Decimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Mapper(componentModel = "spring", imports = {DateUtils.class, PaycheckStatus.class})
public interface MakersPaycheckMapper {
    @Mapping(source = "foodName", target = "name")
    PaycheckDailyFood toPaycheckDailyFood(PaycheckDto.PaycheckDailyFood paycheckDailyFood);

    @Mapping(source = "makers", target = "makers")
    @Mapping(target = "yearMonth", expression = "java(DateUtils.toYearMonth(paycheckDto.getYear(), paycheckDto.getMonth()))")
    @Mapping(target = "paycheckStatus", expression = "java(PaycheckStatus.ofCode(paycheckDto.getPaycheckStatus()))")
    @Mapping(source = "excelFile", target = "excelFile")
    @Mapping(source = "pdfFile", target = "pdfFile")
    MakersPaycheck toEntity(PaycheckDto.MakersRequest paycheckDto, Makers makers, Image excelFile, Image pdfFile);

    @Mapping(source = "paycheckDailyFoods", target = "paycheckDailyFoods")
    @Mapping(source = "makers", target = "makers")
    @Mapping(target = "yearMonth", expression = "java(YearMonth.now())")
    @Mapping(target = "paycheckStatus", constant = "REGISTER")
    MakersPaycheck toInitiateEntity(List<PaycheckDailyFood> paycheckDailyFoods, Makers makers);

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


    default PaycheckDto.PaycheckDailyFoodDto toPaycheckDailyFoodDto(PaycheckDailyFood paycheckDailyFood) {
        PaycheckDto.PaycheckDailyFoodDto paycheckDailyFoodDto = new PaycheckDto.PaycheckDailyFoodDto();
        paycheckDailyFoodDto.setServiceDate(DateUtils.format(paycheckDailyFood.getServiceDate()));
        paycheckDailyFoodDto.setFoodName(paycheckDailyFood.getName());
        paycheckDailyFoodDto.setSupplyPrice(paycheckDailyFood.getSupplyPrice());
        paycheckDailyFoodDto.setCount(paycheckDailyFood.getCount());
        paycheckDailyFoodDto.setTotalPrice(paycheckDailyFood.getSupplyPrice().multiply(BigDecimal.valueOf(paycheckDailyFood.getCount())));
        return paycheckDailyFoodDto;
    }

    default List<PaycheckDto.PaycheckDailyFoodDto> toPaycheckDailyFoodDtos(List<PaycheckDailyFood> paycheckDailyFoods) {
        return paycheckDailyFoods.stream()
                .map(this::toPaycheckDailyFoodDto)
                .toList();
    }

    default PaycheckDto.PaycheckAddDto toPaycheckAddDto(PaycheckAdd paycheckAdd) {
        PaycheckDto.PaycheckAddDto paycheckAddDto = new PaycheckDto.PaycheckAddDto();
        paycheckAddDto.setIssueDate(DateUtils.format(paycheckAdd.getIssueDate()));
        paycheckAddDto.setIssueItem(paycheckAdd.getIssueItem());
        paycheckAddDto.setPaycheckItem(paycheckAdd.getPaycheckItem());
        paycheckAddDto.setPrice(paycheckAdd.getPrice());
        paycheckAddDto.setMemo(paycheckAdd.getMemo());
        return paycheckAddDto;
    }

    default List<PaycheckDto.PaycheckAddDto> toPaycheckAddDtos(List<PaycheckAdd> paycheckAdds) {
        return paycheckAdds.stream()
                .map(this::toPaycheckAddDto)
                .toList();
    }

    default PaycheckDto.MakersDetail toDetailDto(MakersPaycheck makersPaycheck, TransactionInfoDefault transactionInfoDefault) {
        PaycheckDto.MakersDetail makersDetail = new PaycheckDto.MakersDetail();
        List<PaycheckDto.PaycheckDailyFoodDto> paycheckDailyFoodDtos = toPaycheckDailyFoodDtos(makersPaycheck.getPaycheckDailyFoods());

        BigDecimal foodsPrice = paycheckDailyFoodDtos.stream()
                .map(PaycheckDto.PaycheckDailyFoodDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // TODO: 수수료 정리 필요
        double commission = 7.7;
        BigDecimal totalPrice = foodsPrice.multiply(BigDecimal.valueOf((100 - commission) / 100));

        makersDetail.setTransactionInfoDefault(transactionInfoDefault);
        makersDetail.setPaycheckDailyFoods(paycheckDailyFoodDtos);
        makersDetail.setPaycheckAdds(toPaycheckAddDtos(makersPaycheck.getPaycheckAdds()));
        makersDetail.setFoodsPrice(foodsPrice);
        makersDetail.setCommission(commission);
        makersDetail.setTotalPrice(totalPrice);
        return makersDetail;
    }


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
