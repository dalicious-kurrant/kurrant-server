package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.corporation.CorporationApplicationFormResponseDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationMealInfoResponseDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationSpotResponseDto;
import co.dalicious.domain.application_form.entity.CorporationApplicationForm;
import co.dalicious.domain.application_form.entity.CorporationApplicationFormSpot;
import co.dalicious.domain.application_form.entity.CorporationApplicationMealInfo;
import co.dalicious.domain.application_form.entity.enums.PriceAverage;
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
public interface CorporationApplicationFormResMapper {
    @Mapping(source = "createdDateTime", target = "date", qualifiedByName = "createdDateTimeToDate")
    @Mapping(source = "progressStatus", target = "progressStatus", qualifiedByName = "progressStatusToInteger")
    @Mapping(source = "applierName", target = "user.name")
    @Mapping(source = "phone", target = "user.phone")
    @Mapping(source = "email", target = "user.email")
    @Mapping(source = "address", target = "address", qualifiedByName = "addressToString")
    @Mapping(source = "corporationName", target = "corporationInfo.corporationName")
    @Mapping(source = "employeeCount", target = "corporationInfo.employeeCount")
    @Mapping(source = "serviceStartDate", target = "corporationInfo.startDate", qualifiedByName = "dateToString")
    @Mapping(source = "mealInfoList", target = "corporationInfo.diningTypes", qualifiedByName = "getDiningTypes")
    @Mapping(source = "mealInfoList", target = "mealDetails", qualifiedByName = "mealInfoListToDtos")
    @Mapping(source = "isGarbage", target = "option.isGarbage")
    @Mapping(source = "isHotStorage", target = "option.isHotStorage")
    @Mapping(source = "isSetting", target = "option.isSetting")
    @Mapping(source = "memo", target = "option.memo")
    CorporationApplicationFormResponseDto toDto(CorporationApplicationForm corporationApplicationForm);

    @Named("createdDateTimeToDate")
    default String createdDateTimeToDate(Timestamp createdDateTime) {
        return DateUtils.format(createdDateTime, "yyyy. MM. dd");
    }

    @Named("dateToString")
    default String dateToString(LocalDate date) {
        return DateUtils.format(date, "yyyy. MM. dd");
    }

    @Named("getDiningTypes")
    default List<String> getDiningTypes(List<CorporationApplicationMealInfo> mealInfoList) {
        List<String> stringDiningTypes = new ArrayList<>();
        for (CorporationApplicationMealInfo mealInfo : mealInfoList) {
            stringDiningTypes.add(mealInfo.getDiningType().getDiningType());
        }
        return stringDiningTypes;
    }

    @Named("addressToString")
    default String addressToString(Address address) {
        return address.addressToString();
    }

    @Named("progressStatusToInteger")
    default Integer progressStatusToInteger(ProgressStatus progressStatus) {
        return progressStatus.getCode();
    }


    // 식사 정보 toDto
    @Mapping(source = "diningType", target = "diningType", qualifiedByName = "diningTypeToString")
    @Mapping(source = "priceAverage", target = "priceAverage", qualifiedByName = "priceAverageToString")
    @Mapping(source = "serviceDays", target = "serviceDays", qualifiedByName = "serviceDaysToString")
    CorporationMealInfoResponseDto toDto(CorporationApplicationMealInfo corporationApplicationMealInfo);

    @Named("diningTypeToString")
    default String diningTypeToString(DiningType diningType) {
        return diningType.getDiningType();
    }

    @Named("priceAverageToString")
    default String priceAverageToString(PriceAverage priceAverage) {
        return priceAverage.getPriceAverage();
    }
    @Named("serviceDaysToString")
    default String serviceDaysToString(String serviceDays) {
        return DaysUtil.serviceDaysToString(serviceDays);
    }

    @Named("mealInfoListToDtos")
    default List<CorporationMealInfoResponseDto> mealInfoListToDtos(List<CorporationApplicationMealInfo> mealInfoList) {
        List<CorporationMealInfoResponseDto> mealInfoResponseDtos = new ArrayList<>();
        for (CorporationApplicationMealInfo mealInfo : mealInfoList) {
            mealInfoResponseDtos.add(toDto(mealInfo));
        }
        return mealInfoResponseDtos;
    }

    // 스팟 정보 toDto
    @Mapping(source = "name", target = "spotName")
    @Mapping(source = "address", target = "address", qualifiedByName = "addressToString")
    @Mapping(source = "diningTypes", target = "diningTypes", qualifiedByName = "diningTypesToString")
    CorporationSpotResponseDto toDto(CorporationApplicationFormSpot corporationApplicationFormSpot);

    @Named("diningTypesToString")
    default List<String> diningTypesToString(List<DiningType> diningTypes) {
        List<String> strDiningTypes = new ArrayList<>();
        for (DiningType diningType : diningTypes) {
            strDiningTypes.add(diningType.getDiningType());
        }
        return strDiningTypes;
    }
}
