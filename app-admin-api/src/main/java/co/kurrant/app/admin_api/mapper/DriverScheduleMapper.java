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

import java.math.BigInteger;
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
            List<DriverSchedule> selectedDriverSchedules = driverSchedules.stream()
                    .filter(v -> v.getDeliveryDate().equals(key.getServiceDate()) &&
                            v.getDiningType().equals(key.getDiningType()) &&
                            v.getDeliveryTime().equals(key.getDeliveryTime()) &&
                            v.getGroups().contains(key.getGroup()))
                    .toList();
            if (selectedDriverSchedules.isEmpty()) {
                scheduleDtos.add(toScheduleDtoByDailyFood(Objects.requireNonNull(deliveryInfoDtoList)));
                continue;
            }
            List<DriverRoute> driverRoutes = getDriverRoute(deliveryInfoDtoList, selectedDriverSchedules);
            for (DriverRoute driverRoute : driverRoutes) {
                deliveryInfoDtoList.removeIf(v -> v.getMakers().equals(driverRoute.getMakers()) && v.getGroup().equals(driverRoute.getGroup()));
            }
            driverSchedules.removeAll(selectedDriverSchedules);
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
            scheduleDtos.addAll(toScheduleDto(driverSchedule));
        }
        return scheduleDtos;
    }

    default List<ScheduleDto> toScheduleDto(DriverSchedule driverSchedule) {
        List<ScheduleDto> scheduleDtos = new ArrayList<>();
        MultiValueMap<String, String> nameMap = new LinkedMultiValueMap<>();
        driverSchedule.getDriverRoutes()
                .forEach(v -> nameMap.add(v.getGroup().getName(), v.getMakers().getName()));
        int i = 0;
        for (String groupName : nameMap.keySet()) {
            scheduleDtos.add(ScheduleDto.builder()
                    .id(driverSchedule.getId().toString() + "_" + i)
                    .deliveryDate(DateUtils.localDateToString(driverSchedule.getDeliveryDate()))
                    .diningType(driverSchedule.getDiningType().getDiningType())
                    .deliveryTime(DateUtils.timeToString(driverSchedule.getDeliveryTime()))
                    .groupName(groupName)
                    .makersNames(nameMap.get(groupName))
                    .driver(driverSchedule.getDriver().getName())
                    .build());
            i++;
        }
        return scheduleDtos;
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

    default List<DriverRoute> getDriverRoute(List<DeliveryInfoDto> deliveryInfoDtos, Collection<DriverSchedule> driverSchedules) {
        List<Makers> makers = deliveryInfoDtos.stream().map(DeliveryInfoDto::getMakers).toList();
        return driverSchedules.stream()
                .flatMap(driverSchedule -> driverSchedule.getDriverRoutes().stream())
                .filter(driverRoute -> driverRoute.getGroup().equals(deliveryInfoDtos.get(0).getGroup()) &&
                        makers.contains(driverRoute.getMakers()))
                .toList();

    }
}
