package co.dalicious.domain.user.mapper;

import co.dalicious.domain.user.dto.TestDataResponseDto;
import co.dalicious.domain.user.entity.UserTasteTestData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserTasteTestDataMapper {

    @Mapping(source = "userTasteTestData.foodIds", target = "foodIds")
    @Mapping(source = "userTasteTestData.page", target = "pageNum")
    TestDataResponseDto toDto(UserTasteTestData userTasteTestData);
}
