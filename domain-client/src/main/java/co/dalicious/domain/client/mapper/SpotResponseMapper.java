package co.dalicious.domain.client.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.ClientSpotDetailResDto;
import co.dalicious.domain.client.dto.SpotListResponseDto;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.CorporationMealInfo;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SpotResponseMapper {
    @Mapping(source = "id", target = "spotId")
    @Mapping(source = "name", target = "spotName")
    @Mapping(source = "address", target = "address", qualifiedByName = "addressToString")
    @Mapping(source = "group.mealInfos", target = "mealTypeInfoList", qualifiedByName = "getMealTypeInfoList")
    @Mapping(source = "group.name", target = "clientName", qualifiedByName = "addressToString")
    SpotListResponseDto toDto(Spot spot);

    @Named("addressToString")
    default String addressToString(Address address) {
        return address.addressToString();
    }

    @Named("getMealTypeInfoList")
    default List<ClientSpotDetailResDto.MealTypeInfo> getMealTypeInfoList(List<MealInfo> mealInfos) {
        List<ClientSpotDetailResDto.MealTypeInfo> mealTypeInfos = new ArrayList<>();
        for (MealInfo mealInfo : mealInfos) {
            if(mealInfo.getClass().isInstance(CorporationMealInfo.class)) {
                mealTypeInfos.add(
                        ClientSpotDetailResDto.MealTypeInfo.builder()
                                .diningType(mealInfo.getDiningType().getCode())
                                .deliveryTime(DateUtils.timeToString(mealInfo.getDeliveryTime()))
                                .lastOrderTime(DateUtils.timeToString(mealInfo.getLastOrderTime()))
                                .serviceDays(mealInfo.getServiceDays())
                                .build()
                )
            } else {

            }

        }
    }
}
