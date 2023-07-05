package co.dalicious.domain.user.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.domain.user.entity.UserSpot;
import co.dalicious.domain.client.dto.ClientSpotDetailResDto;
import co.dalicious.domain.client.entity.MySpot;
import org.mapstruct.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Mapper(componentModel = "spring")
@Transactional
public interface UserSpotDetailResMapper {

    default ClientSpotDetailResDto toDto(UserSpot spot){
        ClientSpotDetailResDto dto;
        dto = toDtoByOpenSpotOrCorporation(spot);
        dto.setAddress(spot.getSpot().getAddress().addressToString());

        Group group = spot.getSpot().getGroup();
        if(group instanceof MySpotZone mySpotZone) {
            dto.setClientId(mySpotZone.getId());
        }
        else {
            dto.setClientId(group.getId());
            dto.setClientName(group.getName());
        }
        dto.setMealTypeInfoList(getMealTypeInfoList(group.getMealInfos()));

        return dto;
    }

    @Mapping(source = "id", target = "spotId")
    @Mapping(source = "name", target = "spotName")
    @Mapping(target = "address", expression = "java(spot.getAddress().addressToString())")
    ClientSpotDetailResDto toDtoByMySpot(MySpot spot);

    @Mapping(source = "spot.id", target = "spotId")
    @Mapping(source = "spot.name", target = "spotName")
    ClientSpotDetailResDto toDtoByOpenSpotOrCorporation(UserSpot spot);

    default List<ClientSpotDetailResDto.MealTypeInfo> getMealTypeInfoList(List<MealInfo> mealInfos) {
        List<ClientSpotDetailResDto.MealTypeInfo> mealTypeInfos = new ArrayList<>();

        for (MealInfo mealInfo : mealInfos) {
            mealTypeInfos.add(ClientSpotDetailResDto.MealTypeInfo.builder().mealInfo(mealInfo).build());
        }
        mealTypeInfos = mealTypeInfos.stream()
                .sorted(Comparator.comparing(ClientSpotDetailResDto.MealTypeInfo::getDiningType)).toList();

        return mealTypeInfos;
    }
}

