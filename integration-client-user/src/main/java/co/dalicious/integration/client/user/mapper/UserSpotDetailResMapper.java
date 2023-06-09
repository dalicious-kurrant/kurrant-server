package co.dalicious.integration.client.user.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.domain.user.entity.UserSpot;
import co.dalicious.integration.client.user.dto.ClientSpotDetailResDto;
import co.dalicious.integration.client.user.entity.MySpot;
import org.mapstruct.*;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Mapper(componentModel = "spring")
@Transactional
public interface UserSpotDetailResMapper {

    default ClientSpotDetailResDto toDto(UserSpot spot){
        ClientSpotDetailResDto dto;
        if(spot instanceof MySpot mySpot) {
            dto = toDtoByMySpot(mySpot);

            MySpotZone mySpotZone = mySpot.getMySpotZone();
            dto.setClientId(mySpotZone.getId());
            dto.setMealTypeInfoList(getMealTypeInfoList(mySpotZone.getMealInfos()));

            return dto;
        }

        dto = toDtoByOpenSpotOrCorporation(spot);
        dto.setAddress(spot.getSpot().getAddress().addressToString());

        Group group = spot.getSpot().getGroup();
        dto.setClientId(group.getId());
        dto.setClientName(group.getName());
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

