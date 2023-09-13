package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.food.dto.MakersCapacityDto;
import co.dalicious.domain.food.dto.MakersInfoResponseDto;
import co.dalicious.domain.food.entity.*;
import co.dalicious.domain.food.entity.enums.ServiceForm;
import co.dalicious.domain.food.entity.enums.ServiceType;
import co.dalicious.system.enums.Days;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DaysUtil;
import co.kurrant.app.admin_api.dto.MakersDto;
import co.dalicious.domain.food.dto.SaveMakersRequestDto;
import org.locationtech.jts.geom.Geometry;
import org.mapstruct.*;

import javax.inject.Singleton;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {DaysUtil.class, DateUtils.class, DayAndTime.class})
public interface MakersMapper {
    @Mapping(source = "id", target = "makersId")
    @Mapping(source = "name", target = "makersName")
    MakersDto.Makers makersToDto(Makers makers);

    default List<MakersDto.Makers> makersToDtos(Collection<Makers> makers) {
        return makers.stream()
                .map(this::makersToDto)
                .collect(Collectors.toList());
    }

    @Mapping(source = "dto.isNutritionInformation", target = "isNutritionInformation")
    @Mapping(source = "dto.closeTime", target = "closeTime", qualifiedByName = "stringToTimeFormat")
    @Mapping(source = "dto.openTime", target = "openTime", qualifiedByName = "stringToTimeFormat")
    @Mapping(source = "dto.contractEndDate", target = "contractEndDate", qualifiedByName = "stringToDateFormat")
    @Mapping(source = "dto.contractStartDate", target = "contractStartDate", qualifiedByName = "stringToDateFormat")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "dto.serviceForm", target = "serviceForm", qualifiedByName = "getServiceForm")
    @Mapping(source = "dto.serviceType", target = "serviceType", qualifiedByName = "getServiceType")
    @Mapping(source = "dto.managerName", target = "managerName")
    @Mapping(source = "dto.ceoPhone", target = "CEOPhone")
    @Mapping(source = "dto.ceo", target = "CEO")
    @Mapping(source = "dto.companyName", target = "companyName")
    @Mapping(source = "dto.name", target = "name")
    @Mapping(source = "dto.code", target = "code")
    @Mapping(source = "dto.isActive", target = "isActive")
    @Mapping(target = "serviceDays", expression = "java(dto.getServiceDays() == null ? null : DaysUtil.serviceDaysToDaysList(dto.getServiceDays()))")
    @Mapping(target = "introImages", ignore = true)
    Makers toEntity(SaveMakersRequestDto dto, Address address);

    @Named("getServiceType")
    default ServiceType getServiceType(String serviceType){
        return ServiceType.ofString(serviceType);
    }

    @Named("getServiceForm")
    default ServiceForm getServiceForm(String serviceForm){
        return ServiceForm.ofString(serviceForm);
    }

    @Named("stringToTimeFormat")
    default LocalTime stringToTimeFormat(String strTime) {
        return DateUtils.stringToLocalTime(strTime);
    }

    @Named("stringToDateFormat")
    default LocalDate stringToDateFormat(String strDate) {
        return DateUtils.stringToDate(strDate);
    }

    @Mapping(source = "address.location", target = "location", qualifiedByName = "getLocation")
    @Mapping(source = "address.address2", target = "address2")
    @Mapping(source = "address.address1", target = "address1")
    @Mapping(source = "address.zipCode", target = "zipCode")
    @Mapping(source = "serviceForm.serviceForm", target = "serviceForm")
    @Mapping(source = "serviceType.serviceType", target = "serviceType")
    @Mapping(source = "CEOPhone", target = "ceoPhone")
    @Mapping(source = "CEO", target = "ceo")
    @Mapping(source = "makersCapacities", target = "diningTypes", qualifiedByName = "toMakersCapacityDtos")
//    @Mapping(source = "dailyCapacity", target = "dailyCapacity")
    @Mapping(source = "openTime", target = "openTime", qualifiedByName = "timeToStringFormat")
    @Mapping(source = "closeTime", target = "closeTime", qualifiedByName = "timeToStringFormat")
    @Mapping(source = "serviceDays", target = "serviceDays", qualifiedByName = "daysToString")
    @Mapping(source = "introImages", target = "introImages", qualifiedByName = "imageToLocation")
    SaveMakersRequestDto toDto(Makers makers);

    @Mapping(source = "lastOrderTime", target = "lastOrderTime", qualifiedByName = "getLastOrderTime")
    @Mapping(source = "diningType.code", target = "diningType")
    @Mapping(source = "minTime", target = "minTime", qualifiedByName = "timeToStringFormat")
    @Mapping(source = "maxTime", target = "maxTime", qualifiedByName = "timeToStringFormat")
    MakersCapacityDto toMakersCapacityDto(MakersCapacity makersCapacity);

    @Named("daysToString")
    default String daysToString(List<Days> days) {
        return DaysUtil.serviceDaysToDaysString(days);
    }

    @Named("getLocation")
    default String getLocation(Geometry location){
        if (location == null){
            return null;
        }
        return location.toString().substring(7,location.toString().length()-1);
    }

    @Named("getLastOrderTime")
    default String getLastOrderTime(DayAndTime dayAndTime) {
        return DayAndTime.dayAndTimeToString(dayAndTime);
    }

    @Named("timeToStringFormat")
    default String timeToStringFormat(LocalTime time) {
        return DateUtils.timeToString(time);
    }

    @Named("toMakersCapacityDtos")
    default List<MakersCapacityDto> toMakersCapacityDtos(List<MakersCapacity> makersCapacities) {
        return makersCapacities.stream()
                .map(this::toMakersCapacityDto)
                .toList();
    }

    @Named("imageToLocation")
    default List<String> imageToLocation(List<Image> images) {
        if(images != null && !images.isEmpty()) {
            return images.stream().map(Image::getLocation).toList();
        }
        return Collections.emptyList();
    }

    @AfterMapping
    default void afterMapping(@MappingTarget MakersInfoResponseDto dto, Makers makers) {
        dto.setDailyCapacity(makers.getDailyCapacity());
        dto.setDiningTypes(makers.getDiningTypes().stream().map(DiningType::getDiningType).toList());
        toMakersCapacityDto(dto, makers);
    }
    @Named("toMakersCapacityDto")
    default void toMakersCapacityDto(MakersInfoResponseDto dto, Makers makers) {
        MakersCapacity morningCapacity = makers.getMakersCapacity(DiningType.MORNING);
        MakersCapacity lunchCapacity = makers.getMakersCapacity(DiningType.LUNCH);
        MakersCapacity dinnerCapacity = makers.getMakersCapacity(DiningType.DINNER);

        if(morningCapacity != null) {
            dto.setMorningCapacity(morningCapacity.getCapacity());
            dto.setMorningLastOrderTime(DayAndTime.dayAndTimeToString(morningCapacity.getLastOrderTime()));
            dto.setMorningMinTime(DateUtils.timeToString(morningCapacity.getMinTime()));
            dto.setMorningMaxTime(DateUtils.timeToString(morningCapacity.getMaxTime()));
        }
        if(lunchCapacity != null) {
            dto.setLunchCapacity(lunchCapacity.getCapacity());
            dto.setLunchLastOrderTime(DayAndTime.dayAndTimeToString(lunchCapacity.getLastOrderTime()));
            dto.setLunchMinTime(DateUtils.timeToString(lunchCapacity.getMinTime()));
            dto.setLunchMaxTime(DateUtils.timeToString(lunchCapacity.getMaxTime()));
        }
        if(dinnerCapacity != null) {
            dto.setDinnerCapacity(dinnerCapacity.getCapacity());
            dto.setDinnerLastOrderTime(DayAndTime.dayAndTimeToString(dinnerCapacity.getLastOrderTime()));
            dto.setDinnerMinTime(DateUtils.timeToString(dinnerCapacity.getMinTime()));
            dto.setDinnerMaxTime(DateUtils.timeToString(dinnerCapacity.getMaxTime()));
        }
    }

}
