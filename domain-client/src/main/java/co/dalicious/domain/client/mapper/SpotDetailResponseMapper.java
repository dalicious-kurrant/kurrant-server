package co.dalicious.domain.client.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.ClientSpotDetailResDto;
import co.dalicious.domain.client.dto.SpotListResponseDto;
import co.dalicious.domain.client.entity.CorporationMealInfo;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
@Transactional
public interface SpotDetailResponseMapper {
    @Mapping(source = "id", target = "spotId")
    @Mapping(source = "name", target = "spotName")
    @Mapping(source = "address", target = "address", qualifiedByName = "addressToString")
    @Mapping(source = "group.mealInfos", target = "mealTypeInfoList", qualifiedByName = "getMealTypeInfoList")
    @Mapping(source = "group", target = "clientName", qualifiedByName = "getGroupName")
    ClientSpotDetailResDto toDto(Spot spot);

    @Named("addressToString")
    default String addressToString(Address address) {
        return address.addressToString();
    }

    @Named("getGroupName")
    default String getGroupName(Group group) {
        return group.getName();
    }

    @Named("getMealTypeInfoList")
    default List<ClientSpotDetailResDto.MealTypeInfo> getMealTypeInfoList(List<MealInfo> mealInfos) {
        List<ClientSpotDetailResDto.MealTypeInfo> mealTypeInfos = new ArrayList<>();
        for (MealInfo mealInfo : mealInfos) {
            mealTypeInfos.add(ClientSpotDetailResDto.MealTypeInfo.builder().mealInfo(mealInfo).build());
        }
        return mealTypeInfos;
    }
}
