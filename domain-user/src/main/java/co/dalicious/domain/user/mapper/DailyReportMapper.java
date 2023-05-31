package co.dalicious.domain.user.mapper;

import co.dalicious.domain.user.dto.FindDailyReportResDto;
import co.dalicious.domain.user.dto.pointPolicyResponse.SaveDailyReportDto;
import co.dalicious.domain.user.entity.DailyReport;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.enums.DiningType;
import co.kurrant.app.public_api.dto.order.OrderItemDailyFoodToDailyReportDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface DailyReportMapper {


    @Mapping(source = "saveDailyReportDto.name", target = "foodName")
    @Mapping(source = "saveDailyReportDto.calorie", target = "calorie")
    @Mapping(source = "saveDailyReportDto.protein", target = "protein")
    @Mapping(source = "saveDailyReportDto.fat", target = "fat")
    @Mapping(source = "saveDailyReportDto.carbohydrate", target = "carbohydrate")
    @Mapping(source = "saveDailyReportDto.eatDate", target = "eatDate", qualifiedByName = "convertDate")
    @Mapping(source = "saveDailyReportDto.diningType", target = "diningType", qualifiedByName = "generatedDiningType")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "user", target = "user")
    DailyReport toEntity(User user, SaveDailyReportDto saveDailyReportDto, String type, String title);

    @Named("convertDate")
    default LocalDate convertDate(String date){
        return LocalDate.parse(date);
    }

    @Named("generatedDiningType")
    default DiningType generatedDiningType(Integer code){
        return DiningType.ofCode(code);
    }


    @Mapping(source = "dailyReportDto.diningType", target = "diningType")
    @Mapping(source = "dailyReportDto.eatDate", target = "eatDate")
    @Mapping(source = "dailyReportDto.calorie", target = "calorie")
    @Mapping(source = "dailyReportDto.protein", target = "protein")
    @Mapping(source = "dailyReportDto.fat", target = "fat")
    @Mapping(source = "dailyReportDto.carbohydrate", target = "carbohydrate")
    @Mapping(source = "dailyReportDto.title", target = "title")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "dailyReportDto.name", target = "foodName")
    @Mapping(source = "user", target = "user")
    DailyReport toEntityByOrderItemDailyFood(User user, OrderItemDailyFoodToDailyReportDto dailyReportDto, String type);

}
