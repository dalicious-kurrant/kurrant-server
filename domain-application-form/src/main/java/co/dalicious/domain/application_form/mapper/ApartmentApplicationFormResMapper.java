package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.apartment.ApartmentApplicationFormResponseDto;
import co.dalicious.domain.application_form.dto.apartment.ApartmentMealInfoResponseDto;
import co.dalicious.domain.application_form.entity.ApartmentApplicationForm;
import co.dalicious.domain.application_form.entity.ApartmentApplicationMealInfo;
import co.dalicious.domain.application_form.entity.enums.ProgressStatus;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DaysUtil;
import co.dalicious.system.util.DiningType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ApartmentApplicationFormResMapper {
    @Mapping(source = "createdDateTime", target = "date", qualifiedByName = "createdDateTimeToDate")
    @Mapping(source = "progressStatus", target = "progressStatus", qualifiedByName = "progressStatusToInteger")
    @Mapping(source = "applierName", target = "user.name")
    @Mapping(source = "phone", target = "user.phone")
    @Mapping(source = "email", target = "user.email")
    @Mapping(source = "address", target = "address.address")
    @Mapping(source = "apartmentName", target = "info.apartmentName")
    @Mapping(source = "serviceStartDate", target = "info.serviceStartDate", qualifiedByName = "dateToString")
    @Mapping(source = "dongCount", target = "info.dongCount")
    @Mapping(source = "totalFamilyCount", target = "info.familyCount")
    @Mapping(source = "mealInfoList", target = "info.diningTypes", qualifiedByName = "getDiningTypes")
    @Mapping(source = "mealInfoList", target = "meal", qualifiedByName = "mealInfoListToDtos")
    ApartmentApplicationFormResponseDto toDto(ApartmentApplicationForm apartmentApplicationForm);

    @Named("createdDateTimeToDate")
    default String createdDateTimeToDate(Timestamp createdDateTime) {
        return DateUtils.format(createdDateTime, "yyyy. MM. dd");
    }

    @Named("progressStatusToInteger")
    default Integer progressStatusToInteger(ProgressStatus progressStatus) {
        return progressStatus.getCode();
    }

    @Named("addressToString")
    default String addressToString(Address address) {
        return address.addressToString();
    }

    @Named("dateToString")
    default String dateToString(LocalDate date) {
        return DateUtils.format(date, "yyyy. MM. dd");
    }

    @Named("getDiningTypes")
    default List<String> getDiningTypes(List<ApartmentApplicationMealInfo> mealInfoList) {
        List<String> stringDiningTypes = new ArrayList<>();
        for (ApartmentApplicationMealInfo mealInfo : mealInfoList) {
            stringDiningTypes.add(mealInfo.getDiningType().getDiningType());
        }
        return stringDiningTypes;
    }

    @Named("diningTypeToString")
    default String diningTypeToString(DiningType diningType) {
        return diningType.getDiningType();
    }

    @Mapping(source = "diningType", target = "diningType", qualifiedByName = "diningTypeToString")
    @Mapping(source = "expectedUserCount", target = "expectedUserCount")
    @Mapping(source = "serviceDays", target = "serviceDays", qualifiedByName = "serviceDaysToString")
    @Mapping(source = "deliveryTime", target = "deliveryTime")
    ApartmentMealInfoResponseDto toDto(ApartmentApplicationMealInfo mealInfo);

    @Named("mealInfoListToDtos")
    default List<ApartmentMealInfoResponseDto> mealInfoListToDtos(List<ApartmentApplicationMealInfo> mealInfoList) {
        List<ApartmentMealInfoResponseDto> mealInfoResponseDtos = new ArrayList<>();
        for (ApartmentApplicationMealInfo mealInfo : mealInfoList) {
            mealInfoResponseDtos.add(toDto(mealInfo));
        }
        return mealInfoResponseDtos;
    }

    @Named("serviceDaysToString")
    default String serviceDaysToString(List<Integer> serviceDays) {
        return DaysUtil.serviceDaysToDbData(serviceDays);
    }
}

