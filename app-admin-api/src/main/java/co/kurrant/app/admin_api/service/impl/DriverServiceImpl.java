package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.delivery.entity.Driver;
import co.dalicious.domain.delivery.entity.DriverRoute;
import co.dalicious.domain.delivery.entity.DriverSchedule;
import co.dalicious.domain.delivery.entity.enums.DeliveryStatus;
import co.dalicious.domain.delivery.repository.*;
import co.dalicious.domain.food.dto.DeliveryInfoDto;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.food.repository.QMakersRepository;
import co.dalicious.domain.order.dto.OrderDto;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.delivery.DriverDto;
import co.kurrant.app.admin_api.dto.delivery.ScheduleDto;
import co.kurrant.app.admin_api.mapper.DriverScheduleMapper;
import co.kurrant.app.admin_api.service.DriverService;
import exception.ApiException;
import exception.CustomException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final QDailyFoodRepository qDailyFoodRepository;
    private final QDriverScheduleRepository qDriverScheduleRepository;
    private final DriverScheduleMapper driverScheduleMapper;
    private final QMakersRepository qMakersRepository;
    private final QGroupRepository qGroupRepository;
    private final DriverScheduleRepository driverScheduleRepository;
    private final QDriverRepository qDriverRepository;
    private final DriverRouteRepository driverRouteRepository;
    private final QDriverRouteRepository qDriverRouteRepository;

    @Override
    public List<Driver> getDrivers() {
        return driverRepository.findAll();
    }

    @Override
    public void postDrivers(List<DriverDto> driverDtos) {
        List<Driver> drivers = new ArrayList<>();
        driverDtos.forEach(v -> drivers.add(new Driver(v.getName(), v.getCode())));
        driverRepository.saveAll(drivers);
    }

    @Override
    public void deleteDrivers(OrderDto.IdList idList) {
        List<Driver> drivers = driverRepository.findAllById(idList.getIdList());
        for (Driver driver : drivers) {
            try {
                driverRepository.delete(driver);
            } catch (Exception e) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "CE400021", driver.getId() + "번 기사가 삭제되지 않았습니다.");
            }
        }
    }

    @Override
    public List<ScheduleDto> getDriverSchedule(Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));

        List<DeliveryInfoDto> tuples = qDailyFoodRepository.groupingByServiceDateAndRoute(startDate, endDate);
        List<DriverSchedule> driverSchedules = qDriverScheduleRepository.findByPeriod(startDate, endDate);

        return driverScheduleMapper.toScheduleDtos(tuples, driverSchedules);
    }

    @Override
    @Transactional
    public void excelDriverSchedule(List<ScheduleDto> scheduleDtos) {
        Set<BigInteger> driverScheduleIds = scheduleDtos.stream()
                .filter(v -> !v.isTempDto())
                .map(v -> BigInteger.valueOf(Integer.parseInt(v.getId())))
                .collect(Collectors.toSet());
        Set<String> makersNames = scheduleDtos.stream()
                .flatMap(dto -> dto.getMakersNames().stream())
                .collect(Collectors.toSet());
        Set<String> groupNames = scheduleDtos.stream()
                .map(ScheduleDto::getGroupName)
                .collect(Collectors.toSet());
        Set<String> driverNames = scheduleDtos.stream()
                .map(ScheduleDto::getDriver)
                .collect(Collectors.toSet());
        List<Makers> makers = qMakersRepository.getMakersByName(makersNames);
        List<Group> groups = qGroupRepository.findAllByNames(groupNames);
        List<Driver> drivers = qDriverRepository.findAllByDriverNames(driverNames);
        List<DriverSchedule> driverSchedules = driverScheduleRepository.findAllById(driverScheduleIds);

        for (ScheduleDto scheduleDto : scheduleDtos) {
            DriverSchedule driverSchedule = scheduleDto.isTempDto() ?
                    createNewDriverSchedule(scheduleDto, drivers) :
                    getExistingDriverSchedule(driverSchedules, scheduleDto);

            if (scheduleDto.getMakersNames().isEmpty() && !scheduleDto.isTempDto()) {
                driverRouteRepository.deleteAll(driverSchedule.getDriverRoutes());
                continue;
            }
            List<DriverRoute> driverRoutes = getDriverRoutes(scheduleDto, driverSchedule, makers, groups);

            deleteOldRoutes(driverSchedule, driverRoutes);

            driverRouteRepository.saveAll(driverRoutes);
        }
    }

    private DriverSchedule createNewDriverSchedule(ScheduleDto scheduleDto, List<Driver> drivers) {
        return driverScheduleRepository.save(driverScheduleMapper.toDriverSchedule(scheduleDto, drivers));
    }

    private DriverSchedule getExistingDriverSchedule(List<DriverSchedule> driverSchedules, ScheduleDto scheduleDto) {
        return driverSchedules.stream()
                .filter(v -> v.getId().equals(BigInteger.valueOf(Integer.parseInt(scheduleDto.getId()))))
                .findAny().orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
    }

    private List<DriverRoute> getDriverRoutes(ScheduleDto scheduleDto, DriverSchedule driverSchedule, List<Makers> makers, List<Group> groups) {
        return scheduleDto.getMakersNames().stream().map(makersName -> {
            Makers maker = findMakers(makers, makersName);
            Group group = findGroup(groups, scheduleDto.getGroupName());
            return driverSchedule.getDriverRoutes().stream()
                    .filter(route -> route.getMakers().getName().equals(makersName))
                    .findFirst()
                    .orElseGet(() -> createNewDriverRoute(driverSchedule, group, maker));
        }).collect(Collectors.toList());
    }

    private DriverRoute createNewDriverRoute(DriverSchedule driverSchedule, Group group, Makers maker) {
        DriverRoute newDriverRoute = new DriverRoute(DeliveryStatus.WAIT_DELIVERY, group, maker, driverSchedule);
        List<DriverRoute> duplicatedDriverRoutes = qDriverRouteRepository.findAllByDriverRoute(newDriverRoute);
        if(!duplicatedDriverRoutes.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "CE400027", "이미 존재하는 경로입니다.");
        }
        return newDriverRoute;
    }

    private void deleteOldRoutes(DriverSchedule driverSchedule, List<DriverRoute> newRoutes) {
        driverSchedule.getDriverRoutes().stream()
                .filter(oldRoute -> newRoutes.stream().noneMatch(newRoute -> newRoute.getMakers().getName().equals(oldRoute.getMakers().getName())))
                .forEach(driverRouteRepository::delete);
    }

    private Group findGroup(List<Group> groups, String groupName) {
        return groups.stream()
                .filter(group -> group.getName().equals(groupName))
                .findAny()
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "CE400027", groupName + " 이름의 스팟이 존재하지 않습니다."));
    }

    private Makers findMakers(List<Makers> makers, String makersName) {
        return makers.stream()
                .filter(maker -> maker.getName().equals(makersName))
                .findAny()
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "CE400026", makersName + " 이름의 메이커스가 존재하지 않습니다."));
    }

}
