package co.dalicious.domain.review.mapper;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.review.entity.Keyword;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface KeywordMapper {


    @Mapping(source = "name", target = "name")
    @Mapping(source = "i", target = "count")    //초기값 0으로
    @Mapping(source = "food", target = "food")
    Keyword toEntity(String name, int i, Food food);

}
