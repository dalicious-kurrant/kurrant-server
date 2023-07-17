package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.delivery.entity.Driver;
import co.dalicious.domain.delivery.entity.DriverRoute;
import co.dalicious.domain.delivery.entity.DriverSchedule;
import co.dalicious.domain.delivery.entity.enums.DeliveryStatus;
import co.dalicious.domain.food.dto.DeliveryInfoDto;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.delivery.ScheduleDto;
import exception.CustomException;
import org.mapstruct.Mapper;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface DriverScheduleMapper {
    default DriverSchedule toDriverSchedule(ScheduleDto scheduleDto, List<Driver> drivers) {
        Driver driver = drivers.stream()
                .filter(v -> scheduleDto.getDriver().equals(v.getName())).findAny()
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "CE4000024", scheduleDto.getDriver() + " 기사는 존재하지 않습니다."));
        return DriverSchedule.builder()
                .deliveryDate(DateUtils.stringToDate(scheduleDto.getDeliveryDate()))
                .diningType(DiningType.ofString(scheduleDto.getDiningType()))
                .deliveryTime(DateUtils.stringToLocalTime(scheduleDto.getDeliveryTime()))
                .driver(driver)
                .build();
    }

    default List<ScheduleDto> toScheduleDtos(List<DeliveryInfoDto> deliveryInfoDtos, List<DriverSchedule> driverSchedules) {
        List<ScheduleDto> scheduleDtos = new ArrayList<>();
        MultiValueMap<DeliveryInfoDto.Key, DeliveryInfoDto> deliveryInfoDtoMap = new LinkedMultiValueMap<>();
        for (DeliveryInfoDto deliveryInfoDto : deliveryInfoDtos) {
            deliveryInfoDtoMap.add(new DeliveryInfoDto.Key(deliveryInfoDto), deliveryInfoDto);
        }
        for (DeliveryInfoDto.Key key : deliveryInfoDtoMap.keySet()) {
            List<DeliveryInfoDto> deliveryInfoDtoList = deliveryInfoDtoMap.get(key);
            driverSchedules = driverSchedules.stream()
                    .filter(v -> v.getDeliveryDate().equals(key.getServiceDate()) &&
                            v.getDiningType().equals(key.getDiningType()) &&
                            v.getDeliveryTime().equals(key.getDeliveryTime()))
                    .toList();
            if (driverSchedules.isEmpty()) {
                scheduleDtos.add(toScheduleDtoByDailyFood(Objects.requireNonNull(deliveryInfoDtoList)));
                continue;
            }
            List<DriverRoute> driverRoutes = getDriverRoute(deliveryInfoDtoList, driverSchedules);
            List<Makers> makers = driverRoutes.stream().map(DriverRoute::getMakers).toList();
            deliveryInfoDtoList.removeIf(v -> makers.contains(v.getMakers()));
            scheduleDtos.addAll(toScheduleDtoByDriverRoute(driverRoutes));
        }
        return scheduleDtos;
    }

    default ScheduleDto toScheduleDtoByDailyFood(List<DeliveryInfoDto> deliveryInfoDto) {
        List<String> makersNames = deliveryInfoDto.stream()
                .map(v -> v.getMakers().getName())
                .toList();

        return ScheduleDto.builder()
                .id(generateTempId(deliveryInfoDto.get(0)))
                .deliveryDate(DateUtils.localDateToString(deliveryInfoDto.get(0).getServiceDate()))
                .diningType(deliveryInfoDto.get(0).getDiningType().getDiningType())
                .deliveryTime(DateUtils.timeToString(deliveryInfoDto.get(0).getDeliveryTime()))
                .groupName(deliveryInfoDto.get(0).getGroup().getName())
                .makersNames(makersNames)
                .driver(null)
                .build();
    }

    default String generateTempId(DeliveryInfoDto deliveryInfoDto) {
        return "temp"
        + DateUtils.formatWithoutSeparator(deliveryInfoDto.getServiceDate())
                + deliveryInfoDto.getDiningType().getCode() + deliveryInfoDto.getDeliveryTime().getHour()
                + deliveryInfoDto.getDeliveryTime().getMinute()
                + deliveryInfoDto.getGroup().getId() + "_1";
    }

    default List<ScheduleDto> toScheduleDtoByDriverRoute(List<DriverRoute> driverRoutes) {
        List<ScheduleDto> scheduleDtos = new ArrayList<>();
        Set<DriverSchedule> driverScheduleSet = driverRoutes.stream().map(DriverRoute::getDriverSchedule)
                .collect(Collectors.toSet());
        for (DriverSchedule driverSchedule : driverScheduleSet) {
            scheduleDtos.add(toScheduleDto(driverSchedule));
        }
        return scheduleDtos;
    }

    default ScheduleDto toScheduleDto(DriverSchedule driverSchedule) {
        List<String> makersNames = driverSchedule.getDriverRoutes().stream()
                .map(v -> v.getMakers().getName())
                .toList();
        return ScheduleDto.builder()
                .id(driverSchedule.getId().toString())
                .deliveryDate(DateUtils.localDateToString(driverSchedule.getDeliveryDate()))
                .diningType(driverSchedule.getDiningType().getDiningType())
                .deliveryTime(DateUtils.timeToString(driverSchedule.getDeliveryTime()))
                .groupName(driverSchedule.getDriverRoutes().get(0).getGroup().getName())
                .makersNames(makersNames)
                .driver(driverSchedule.getDriver().getName())
                .build();
    }

    default DriverRoute getDriverSchedule(DeliveryInfoDto deliveryInfoDto, List<DriverSchedule> driverSchedules) {
        return driverSchedules.stream()
                .filter(driverSchedule -> driverSchedule.getDeliveryDate().equals(deliveryInfoDto.getServiceDate()) &&
                        driverSchedule.getDiningType().equals(deliveryInfoDto.getDiningType()) &&
                        driverSchedule.getDeliveryTime().equals(deliveryInfoDto.getDeliveryTime()))
                .flatMap(driverSchedule -> driverSchedule.getDriverRoutes().stream())
                .filter(driverRoute -> driverRoute.getGroup().equals(deliveryInfoDto.getGroup()) &&
                        driverRoute.getMakers().equals(deliveryInfoDto.getMakers()))
                .findAny()
                .orElse(null);
    }

    default List<DriverRoute> getDriverRoute(List<DeliveryInfoDto> deliveryInfoDtos, List<DriverSchedule> driverSchedules) {
        List<Makers> makers = deliveryInfoDtos.stream().map(DeliveryInfoDto::getMakers).toList();
        return driverSchedules.stream()
                .flatMap(driverSchedule -> driverSchedule.getDriverRoutes().stream())
                .filter(driverRoute -> driverRoute.getGroup().equals(deliveryInfoDtos.get(0).getGroup()) &&
                        makers.contains(driverRoute.getMakers()))
                .toList();

    }
}
