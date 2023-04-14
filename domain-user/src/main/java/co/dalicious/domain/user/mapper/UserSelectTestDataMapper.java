package co.dalicious.domain.user.mapper;

import co.dalicious.domain.user.dto.UserPreferenceDto;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserPreference;
import co.dalicious.domain.user.entity.UserSelectTestData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import javax.transaction.Transactional;
import java.math.BigInteger;

@Mapper(componentModel = "spring")
@Transactional
public interface UserSelectTestDataMapper {


    @Mapping(source = "user", target = "user")
    @Mapping(source = "userPreference", target = "userPreference")
    @Mapping(source = "selectedFoodId", target = "selectedFoodIds")
    @Mapping(source = "unselectedFoodId", target = "unselectedFoodIds")
    UserSelectTestData toEntity(String selectedFoodId, String unselectedFoodId, UserPreference userPreference, User user);
}
