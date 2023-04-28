package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.SpotResponseDto;
import co.dalicious.domain.client.dto.UpdateSpotDetailRequestDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.embeddable.ServiceDaysAndSupportPrice;
import co.dalicious.domain.client.entity.enums.PaycheckCategoryItem;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.enums.Days;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;

import co.dalicious.system.util.DaysUtil;
import co.kurrant.app.admin_api.dto.client.SpotDetailResDto;
import exception.ApiException;
import exception.ExceptionEnum;
import org.hibernate.Hibernate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {DateUtils.class, Address.class, Group.class, MealInfo.class})
public interface SpotMapper {

    default SpotResponseDto toDto(Spot spot) {
        SpotResponseDto spotResponseDto = new SpotResponseDto();
        boolean isCorporation = spot instanceof CorporationSpot;
        spotResponseDto.setSpotId(spot.getId());
        spotResponseDto.setStatus(spot.getStatus().getCode());
        spotResponseDto.setSpotName(spot.getName());
        spotResponseDto.setGroupId(spot.getGroup().getId());
        spotResponseDto.setGroupName(spot.getGroup().getName());
        spotResponseDto.setZipCode(spot.getAddress().getZipCode());
        spotResponseDto.setAddress1(spot.getAddress().getAddress1());
        spotResponseDto.setAddress2(spot.getAddress().getAddress2());
        spotResponseDto.setLocation(getLocation(spot.getAddress().getLocation()));

        spotResponseDto.setBreakfastLastOrderTime(spot.getMealInfo(DiningType.MORNING) == null ? null : DayAndTime.dayAndTimeToString(spot.getMealInfo(DiningType.MORNING).getLastOrderTime()));
        spotResponseDto.setBreakfastDeliveryTime(spot.getMealInfo(DiningType.MORNING) == null ? null : DateUtils.timeToString(spot.getMealInfo(DiningType.MORNING).getDeliveryTime()));
        spotResponseDto.setBreakfastUseDays(spot.getMealInfo(DiningType.MORNING) == null ? null : DaysUtil.serviceDaysToDaysString(spot.getMealInfo(DiningType.MORNING).getServiceDays()));
        spotResponseDto.setBreakfastSupportPrice(BigDecimal.ZERO);
        spotResponseDto.setBreakfastMembershipBenefitTime(spot.getMealInfo(DiningType.MORNING) == null ? null : spot.getMealInfo(DiningType.MORNING).dayAndTimeToString());

        spotResponseDto.setLunchLastOrderTime(spot.getMealInfo(DiningType.LUNCH) == null ? null : DayAndTime.dayAndTimeToString(spot.getMealInfo(DiningType.LUNCH).getLastOrderTime()));
        spotResponseDto.setLunchDeliveryTime(spot.getMealInfo(DiningType.LUNCH) == null ? null : DateUtils.timeToString(spot.getMealInfo(DiningType.LUNCH).getDeliveryTime()));
        spotResponseDto.setLunchUseDays(spot.getMealInfo(DiningType.LUNCH) == null ? null : DaysUtil.serviceDaysToDaysString(spot.getMealInfo(DiningType.LUNCH).getServiceDays()));
        spotResponseDto.setLunchSupportPrice(BigDecimal.ZERO);
        spotResponseDto.setLunchMembershipBenefitTime(spot.getMealInfo(DiningType.LUNCH) == null ? null : spot.getMealInfo(DiningType.LUNCH).dayAndTimeToString());

        spotResponseDto.setDinnerLastOrderTime(spot.getMealInfo(DiningType.DINNER) == null ? null : DayAndTime.dayAndTimeToString(spot.getMealInfo(DiningType.DINNER).getLastOrderTime()));
        spotResponseDto.setDinnerDeliveryTime(spot.getMealInfo(DiningType.DINNER) == null ? null : DateUtils.timeToString(spot.getMealInfo(DiningType.DINNER).getDeliveryTime()));
        spotResponseDto.setDinnerUseDays(spot.getMealInfo(DiningType.DINNER) == null ? null : DaysUtil.serviceDaysToDaysString(spot.getMealInfo(DiningType.DINNER).getServiceDays()));
        spotResponseDto.setDinnerSupportPrice(BigDecimal.ZERO);
        spotResponseDto.setDinnerMembershipBenefitTime((spot.getMealInfo(DiningType.DINNER) == null ? null : spot.getMealInfo(DiningType.DINNER).dayAndTimeToString()));

        spotResponseDto.setCreatedDateTime(DateUtils.format(spot.getCreatedDateTime().toLocalDateTime().toLocalDate()));
        spotResponseDto.setUpdatedDateTime(DateUtils.format(spot.getUpdatedDateTime().toLocalDateTime().toLocalDate()));


        StringJoiner diningTypes = new StringJoiner(", ");
        // 상세 스팟에 식사 정보가 없기 때문에 다이닝 타입만 찾아서 다이닝 타입 보내기
        List<DiningType> diningTypeList = spot.getDiningTypes();
        for (DiningType diningType : diningTypeList) {
            diningTypes.add(diningType.getDiningType());
        }
        String diningTypesStr = diningTypes.toString();

        spotResponseDto.setDiningType(diningTypesStr);
        return spotResponseDto;
    }

    default MealInfo toMealInfo(Group group, DiningType diningType, String lastOrderTime, String deliveryTime, String useDays, String membershipBenefitTime) {
        // MealInfo 를 생성하기 위한 기본값이 존재하지 않으면 객체 생성 X
        if (lastOrderTime == null || deliveryTime == null || useDays == null) {
            return null;
        }
        // 기업 스팟인 경우
        if (group instanceof Corporation corporation) {
            return CorporationMealInfo.builder()
                    .group(corporation)
                    .diningType(diningType)
                    .lastOrderTime(DayAndTime.stringToDayAndTime(lastOrderTime))
                    .deliveryTime(DateUtils.stringToLocalTime(deliveryTime))
                    .serviceDays(DaysUtil.serviceDaysToDaysList(useDays))
                    .membershipBenefitTime(MealInfo.stringToDayAndTime(membershipBenefitTime))
                    .build();
        } else if (group instanceof Apartment apartment) {
            return ApartmentMealInfo.builder()
                    .group(apartment)
                    .diningType(diningType)
                    .lastOrderTime(DayAndTime.stringToDayAndTime(lastOrderTime))
                    .deliveryTime(DateUtils.stringToLocalTime(deliveryTime))
                    .membershipBenefitTime(MealInfo.stringToDayAndTime(membershipBenefitTime))
                    .serviceDays(DaysUtil.serviceDaysToDaysList(useDays))
                    .build();
        } else if (group instanceof OpenGroup openGroup) {
            return OpenGroupMealInfo.builder()
                    .group(openGroup)
                    .diningType(diningType)
                    .lastOrderTime(DayAndTime.stringToDayAndTime(lastOrderTime))
                    .deliveryTime(DateUtils.stringToLocalTime(deliveryTime))
                    .membershipBenefitTime(MealInfo.stringToDayAndTime(membershipBenefitTime))
                    .serviceDays(DaysUtil.serviceDaysToDaysList(useDays))
                    .build();

        }
        return null;
    }


    @Named("createdTimeFormat")
    default String createdTimeFormat(Timestamp time) {
        return DateUtils.format(time, "yyyy-MM-dd");
    }

    @Named("updatedTimeFormat")
    default String updatedTimeFormat(Timestamp time) {
        return DateUtils.format(time, "yyyy-MM-dd");
    }

    @Named("getLocation")
    default String getLocation(Geometry location) {
        if (location == null) {
            return null;
        }
        return location.toString();
    }


    @Mapping(source = "address", target = "address")
    @Mapping(source = "spotInfo.groupId", target = "group", qualifiedByName = "generatedGroup")
    @Mapping(source = "spotInfo.spotName", target = "name")
    @Mapping(source = "diningTypes", target = "diningTypes")
    @Mapping(source = "spotInfo.memo", target = "memo")
    Spot toEntity(SpotResponseDto spotInfo, Address address, List<DiningType> diningTypes);


    default Spot toEntity(SpotResponseDto spotInfo, Group group, List<DiningType> diningTypes) throws ParseException {
        if (group == null) {
            throw new IllegalArgumentException("상세스팟 아이디:" + spotInfo.getSpotId().toString() + " 등록되어있지 않은 그룹입니다.");
        }
        Set<DiningType> groupDiningTypes = new HashSet<>(group.getDiningTypes());
        if (!groupDiningTypes.containsAll(diningTypes)) {
            throw new ApiException(ExceptionEnum.GROUP_DOSE_NOT_HAVE_DINING_TYPE);
        }
        //TODO: Location 생성
        String location = spotInfo.getLocation();
        Address address = new Address(spotInfo.getZipCode(), spotInfo.getAddress1(), spotInfo.getAddress2(), location);
        if (group instanceof Apartment)
            return new ApartmentSpot(spotInfo.getSpotName(), address, diningTypes, group, spotInfo.getMemo());
        if (group instanceof Corporation)
            return new CorporationSpot(spotInfo.getSpotName(), address, diningTypes, group, spotInfo.getMemo());
        return null;
    }

    @Named("generatedGroup")
    default Group generatedGroup(BigInteger groupId) {
        return new Group(groupId);
    }

    @Mapping(source = "group.name", target = "name")
    @Mapping(source = "group.address", target = "address")
    @Mapping(source = "group.diningTypes", target = "diningTypes")
    @Mapping(source = "group", target = "group")
    CorporationSpot toCorporationSpotEntity(Group group);

    @Mapping(source = "group.name", target = "name")
    @Mapping(source = "group.address", target = "address")
    @Mapping(source = "group.diningTypes", target = "diningTypes")
    @Mapping(source = "group", target = "group")
    ApartmentSpot toApartmentSpotEntity(Group group);

    @Mapping(source = "group.name", target = "name")
    @Mapping(source = "group.address", target = "address")
    @Mapping(source = "group.diningTypes", target = "diningTypes")
    @Mapping(source = "group", target = "group")
    OpenGroupSpot toOpenGroupSpotEntity(Group group);


    default SpotDetailResDto toDetailDto(Spot spot, User manager, List<MealInfo> mealInfoList) {
        SpotDetailResDto spotDetailResDto = new SpotDetailResDto();

        spotDetailResDto.setGroupId(spot.getGroup().getId());
        spotDetailResDto.setSpotName(spot.getName());
        spotDetailResDto.setManagerId(manager.getId());
        spotDetailResDto.setManagerName(manager.getName());
        spotDetailResDto.setManagerPhone(manager.getPhone());
        spotDetailResDto.setSpotName(spot.getName());
        spotDetailResDto.setZipCode(spot.getAddress().getZipCode());
        spotDetailResDto.setAddress1(spot.getAddress().getAddress1());
        spotDetailResDto.setAddress2(spot.getAddress().getAddress2());
        if (spot.getAddress().getLocation() == null) {
            spotDetailResDto.setLocation("없음");
        } else {
            spotDetailResDto.setLocation(spot.getAddress().getLocation().toString().substring(7, (spot.getAddress().getLocation().toString().length() - 1)));
        }
        spotDetailResDto.setMemo(spot.getMemo());

        List<Integer> types = new ArrayList<>();
        for (DiningType type : spot.getDiningTypes()) {
            types.add(type.getCode());
        }
        spotDetailResDto.setDiningTypes(types.toString().substring(1, types.toString().length() - 1));

        if (spot instanceof CorporationSpot) {
            spotDetailResDto.setSpotType("Corporation");
        } else if (spot instanceof OpenGroupSpot) {
            spotDetailResDto.setSpotType("OpenGroup");
        } else if (spot instanceof ApartmentSpot) {
            spotDetailResDto.setSpotType("Apartment");
        } else {
            spotDetailResDto.setSpotType("없음");
        }

        spotDetailResDto.setMemo(spot.getMemo());

        if (Hibernate.unproxy(spot.getGroup()) instanceof Corporation corporation) {
            spotDetailResDto.setCode(corporation.getCode());
            spotDetailResDto.setExpectedCount(corporation.getEmployeeCount());
            spotDetailResDto.setIsSetting(corporation.getIsSetting());
            spotDetailResDto.setIsHotStorage(corporation.getIsHotStorage());
            spotDetailResDto.setIsGarbage(corporation.getIsGarbage());
            spotDetailResDto.setIsMembershipSupport(corporation.getIsMembershipSupport());
            spotDetailResDto.setIsPrepaid(corporation.getIsPrepaid());

            if(corporation.getIsPrepaid() && (corporation.getPrepaidCategories() != null || !corporation.getPrepaidCategories().isEmpty())) {
                spotDetailResDto.setPrepaidCategoryList(toPrepaidCategoryDtos(corporation.getPrepaidCategories()));
            }
            if (corporation.getMinimumSpend() != null)
                spotDetailResDto.setMinPrice(corporation.getMinimumSpend().intValue());
            if (corporation.getMaximumSpend() != null)
                spotDetailResDto.setMaxPrice(corporation.getMaximumSpend().intValue());
        }


        if(mealInfoList != null && ! mealInfoList.isEmpty()) {
            LinkedHashSet<Days> serviceDays = new LinkedHashSet<>();
            LinkedHashSet<Days> supportDays = new LinkedHashSet<>();
            for (MealInfo mealInfo : mealInfoList) {
                serviceDays.addAll(mealInfo.getServiceDays());
                if (mealInfo instanceof CorporationMealInfo corporationMealInfo) {
                    List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPriceList = corporationMealInfo.getServiceDaysAndSupportPrices();
                    for (ServiceDaysAndSupportPrice serviceDaysAndSupportPrice : serviceDaysAndSupportPriceList) {
                        BigDecimal supportPrice = serviceDaysAndSupportPrice.getSupportPrice();
                        supportDays.addAll(serviceDaysAndSupportPrice.getSupportDays());

                        switch (mealInfo.getDiningType()) {
                            case MORNING -> spotDetailResDto.setBreakfastSupportPrice(supportPrice);
                            case LUNCH -> spotDetailResDto.setLunchSupportPrice(supportPrice);
                            case DINNER -> spotDetailResDto.setDinnerSupportPrice(supportPrice);
                        }
                    }
                }
            }
            List<Days> notSupportDays = new ArrayList<>(serviceDays);
            notSupportDays.removeAll(supportDays);

            spotDetailResDto.setSupportDays(DaysUtil.serviceDaysSetToString(supportDays));
            spotDetailResDto.setMealDay(DaysUtil.serviceDaysSetToString(serviceDays));
            spotDetailResDto.setNotSupportDays(DaysUtil.serviceDaysToDaysString(notSupportDays));
        }
        return spotDetailResDto;
    }

    default SpotDetailResDto.PrepaidCategory toPrepaidCategoryDto(PrepaidCategory prepaidCategory) {
        return SpotDetailResDto.PrepaidCategory.builder()
                .paycheckCategoryItem(prepaidCategory.getPaycheckCategoryItem().getPaycheckCategoryItem())
                .count(prepaidCategory.getCount())
                .price(prepaidCategory.getPrice() == null ? null : prepaidCategory.getPrice().intValue())
                .totalPrice(prepaidCategory.getTotalPrice() == null ? null : prepaidCategory.getTotalPrice().intValue())
                .build();
    }

    default List<SpotDetailResDto.PrepaidCategory> toPrepaidCategoryDtos(List<PrepaidCategory> prepaidCategoryList) {
        return prepaidCategoryList.stream()
                .map(this::toPrepaidCategoryDto)
                .toList();
    }

    default PrepaidCategory toPrepaidCategory(UpdateSpotDetailRequestDto.PrepaidCategory prepaidCategoryDto) {
        return new PrepaidCategory(PaycheckCategoryItem.ofCode(prepaidCategoryDto.getCode()), prepaidCategoryDto.getCount(), BigDecimal.valueOf(prepaidCategoryDto.getPrice()), BigDecimal.valueOf(prepaidCategoryDto.getTotalPrice()));
    }

    default List<PrepaidCategory> toPrepaidCategories(List<UpdateSpotDetailRequestDto.PrepaidCategory> prepaidCategoryDtos) {
        return prepaidCategoryDtos.stream()
                .map(this::toPrepaidCategory)
                .toList();
    }
}

