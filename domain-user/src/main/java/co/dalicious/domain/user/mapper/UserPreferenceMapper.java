package co.dalicious.domain.user.mapper;

import co.dalicious.domain.user.dto.UserPreferenceDto;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserPreference;
import co.dalicious.domain.user.entity.enums.Country;
import co.dalicious.domain.user.entity.enums.JobType;
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
    @Mapping(source = "userPreferenceDto.userDefaultInfo.country", target = "country", qualifiedByName = "generatedCountry")
    @Mapping(source = "userPreferenceDto.userDefaultInfo.birthPlace", target = "birthPlace")
    @Mapping(source = "userPreferenceDto.userDefaultInfo.jobType", target = "jobType", qualifiedByName = "generatedJobType")
    @Mapping(source = "userPreferenceDto.userDefaultInfo.detailJobType", target = "detailJobType", qualifiedByName = "generatedJobType")
    @Mapping(source = "userPreferenceDto.userDefaultInfo.gender", target = "gender")
    @Mapping(source = "userPreferenceDto.userDefaultInfo.birthYear", target = "birthYear")
    @Mapping(source = "userPreferenceDto.userDefaultInfo.birthMonth", target = "birthMonth")
    @Mapping(source = "userPreferenceDto.userDefaultInfo.birthDay", target = "birthDay")
    @Mapping(source = "userPreferenceDto.proteinFrequency", target = "proteinFrequency")
    @Mapping(source = "userPreferenceDto.isProtein", target = "isProtein")
    @Mapping(source = "userPreferenceDto.veganLevel", target = "veganLevel")
    @Mapping(source = "userPreferenceDto.isBegan", target = "isBegan")
    @Mapping(source = "userPreferenceDto.allergyInfo", target = "allergyInfo", qualifiedByName = "makeFoodTagList")
    @Mapping(source = "userPreferenceDto.allergyInfoEtc", target = "allergyInfoEtc")
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
            result.add(FoodTag.ofString(foodTagString));
        }
        return result;
    }

    @Named("generatedCountry")
    default Country generatedCountry(String country){
        return Country.ofValue(country);
    }

    @Named("generatedJobType")
    default JobType generatedJobType(String jobType){
        return JobType.ofValue(jobType);
    }

}
