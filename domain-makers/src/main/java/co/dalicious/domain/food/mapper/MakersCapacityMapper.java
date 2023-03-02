package co.dalicious.domain.food.mapper;

import co.dalicious.domain.food.dto.MakersCapacityDto;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.MakersCapacity;
import co.dalicious.system.enums.DiningType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MakersCapacityMapper {

    @Mapping(source = "diningTypes.capacity", target = "capacity")
    @Mapping(source = "diningTypes.diningType", target = "diningType", qualifiedByName = "getDiningType")
    @Mapping(source = "makers", target = "makers")
    MakersCapacity toEntity(Makers makers, MakersCapacityDto diningTypes);

    @Named("getDiningType")
    default DiningType getDiningType(Integer diningType){
        return DiningType.ofCode(diningType);
    }

}
