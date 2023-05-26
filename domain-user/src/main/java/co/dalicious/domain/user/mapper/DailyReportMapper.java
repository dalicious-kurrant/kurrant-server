package co.dalicious.domain.user.mapper;

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
    @Mapping(source = "type", target = "type")
    @Mapping(source = "user", target = "user")
    DailyReport toEntity(User user, SaveDailyReportDto saveDailyReportDto, String type);

    @Named("convertDate")
    default LocalDate convertDate(String date){
        return LocalDate.parse(date);
    }

    @Named("generatedDiningType")
    default DiningType generatedDiningType(Integer code){
        return DiningType.ofCode(code);
    }

}
