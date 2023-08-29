package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.delivery.dto.DeliveryInstanceDto;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.delivery.entity.Driver;
import co.dalicious.domain.delivery.mappper.DeliveryInstanceMapper;
import co.dalicious.domain.delivery.repository.*;
import co.dalicious.domain.food.dto.DeliveryInfoDto;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.food.repository.QMakersRepository;
import co.dalicious.domain.order.dto.OrderDto;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.delivery.DriverDto;
import co.kurrant.app.admin_api.service.DriverService;
import exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final QDailyFoodRepository qDailyFoodRepository;
    private final QMakersRepository qMakersRepository;
    private final QGroupRepository qGroupRepository;
    private final QDriverRepository qDriverRepository;
    private final DeliveryInstanceMapper deliveryInstanceMapper;
    private final QDeliveryInstanceRepository qDeliveryInstanceRepository;
    private final DeliveryInstanceRepository deliveryInstanceRepository;


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
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<DeliveryInstanceDto> getDriverSchedule(Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));

        List<DeliveryInfoDto> tuples = qDailyFoodRepository.groupingByServiceDateAndRoute(startDate, endDate);
        List<DeliveryInstance> deliveryInstances = qDeliveryInstanceRepository.findByPeriod(startDate, endDate);

        return deliveryInstanceMapper.toScheduleDtos(tuples, deliveryInstances);
    }

    @Override
    @Transactional
    public void postDriverSchedule(List<DeliveryInstanceDto> deliveryInstanceDtos) {
        Set<String> driverNames = deliveryInstanceDtos.stream()
                .map(DeliveryInstanceDto::getDriver)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<String> makersNames = deliveryInstanceDtos.stream()
                .flatMap(dto -> dto.getMakersNames().stream())
                .collect(Collectors.toSet());
        Set<String> groupNames = deliveryInstanceDtos.stream()
                .map(DeliveryInstanceDto::getGroupName)
                .collect(Collectors.toSet());
        List<Driver> drivers = (driverNames.isEmpty()) ? Collections.emptyList() : qDriverRepository.findAllByDriverNames(driverNames);
        List<Makers> makers = qMakersRepository.getMakersByName(makersNames);
        List<Group> groups = qGroupRepository.findAllByNames(groupNames);
        for (DeliveryInstanceDto scheduleDto : deliveryInstanceDtos) {
            List<DeliveryInstance> deliveryInstances = qDeliveryInstanceRepository.findAllBy(DateUtils.stringToDate(scheduleDto.getDeliveryDate()), DiningType.ofString(scheduleDto.getDiningType()), DateUtils.stringToLocalTime(scheduleDto.getDeliveryTime()), scheduleDto.getMakersNames(), scheduleDto.getGroupName());
            if (scheduleDto.isTempDto() && deliveryInstances.isEmpty()) {
                List<DeliveryInstance> newDeliveryInstances = deliveryInstanceMapper.toEntities(scheduleDto, makers, groups);
                deliveryInstanceRepository.saveAll(newDeliveryInstances);
            }
            Driver driver = drivers.stream().filter(v -> v.getName().equals(scheduleDto.getDriver())).findAny().orElse(null);
            deliveryInstances.forEach(v -> v.updateDriver(driver));
        }
    }
}
