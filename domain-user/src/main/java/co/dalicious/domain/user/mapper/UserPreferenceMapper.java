package co.dalicious.domain.user.mapper;

import co.dalicious.domain.user.dto.UserPreferenceDto;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserPreference;
import co.dalicious.system.enums.FoodTag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserPreferenceMapper {

    @Mapping(source = "userPreferenceDto.selectedFoodId", target = "selectedFoodId")
    @Mapping(source = "userPreferenceDto.unselectedFoodId", target = "unselectedFoodId")
    @Mapping(source = "userPreferenceDto.userDefaultInfo.country", target = "country")
    @Mapping(source = "userPreferenceDto.userDefaultInfo.birthPlace", target = "birthPlace")
    @Mapping(source = "userPreferenceDto.userDefaultInfo.jobType", target = "jobType")
    @Mapping(source = "userPreferenceDto.userDefaultInfo.gender", target = "gender")
    @Mapping(source = "userPreferenceDto.userDefaultInfo.birthYear", target = "birthYear")
    @Mapping(source = "userPreferenceDto.userDefaultInfo.birthMonth", target = "birthMonth")
    @Mapping(source = "userPreferenceDto.userDefaultInfo.birthDay", target = "birthDay")
    @Mapping(source = "userPreferenceDto.proteinScoop", target = "proteinScoop")
    @Mapping(source = "userPreferenceDto.proteinBarFrequency", target = "proteinBarFrequency")
    @Mapping(source = "userPreferenceDto.proteinDrinkFrequency", target = "proteinDrinkFrequency")
    @Mapping(source = "userPreferenceDto.isProtein", target = "isProtein")
    @Mapping(source = "userPreferenceDto.beganLevel", target = "beganLevel")
    @Mapping(source = "userPreferenceDto.isBegan", target = "isBegan")
    @Mapping(source = "userPreferenceDto.allergyInfo", target = "allergyInfo", qualifiedByName = "makeFoodTagList")
    @Mapping(source = "userPreferenceDto.favoriteCountryFood", target = "favoriteCountryFood", qualifiedByName = "makeFoodTagList")
    @Mapping(source = "userPreferenceDto.breakfastCount", target = "breakfastCount")
    @Mapping(source = "userPreferenceDto.midnightSnackCount", target = "midnightSnackCount")
    @Mapping(source = "userPreferenceDto.exerciseCount", target = "exerciseCount")
    @Mapping(source = "userPreferenceDto.drinkCount", target = "drinkCount")
    @Mapping(source = "user", target = "user")
    UserPreference toEntity(User user, UserPreferenceDto userPreferenceDto);

    @Named("makeFoodTagList")
    default List<FoodTag> makeFoodTagList(String foodTags){
        List<FoodTag> result = new ArrayList<>();
        List<String> split = Arrays.stream(foodTags.split(",")).toList();
        for (String foodTagString : split ){
            result.add(FoodTag.ofCode(Integer.valueOf(foodTagString)));
        }
        return result;
    }

}