package co.dalicious.domain.user.mapper;

import co.dalicious.domain.user.dto.FindDailyReportResDto;
import co.dalicious.domain.user.dto.pointPolicyResponse.SaveDailyReportDto;
import co.dalicious.domain.user.entity.DailyReport;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.enums.DiningType;
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


    @Mapping(source = "diningType", target = "diningType")
    @Mapping(source = "eatDate", target = "eatDate")
    @Mapping(source = "calorie", target = "calorie")
    @Mapping(source = "protein", target = "protein")
    @Mapping(source = "fat", target = "fat")
    @Mapping(source = "carbohydrate", target = "carbohydrate")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "name", target = "foodName")
    @Mapping(source = "user", target = "user")
    DailyReport toEntityByOrderItemDailyFood(User user, String name, Integer carbohydrate, Integer fat, Integer protein, Integer calorie, LocalDate eatDate, DiningType diningType, String type, String title);

}
