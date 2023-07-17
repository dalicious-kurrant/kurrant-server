package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.delivery.entity.Driver;
import co.dalicious.domain.delivery.entity.DriverSchedule;
import co.dalicious.domain.delivery.repository.DriverRepository;
import co.dalicious.domain.delivery.repository.QDriverScheduleRepository;
import co.dalicious.domain.food.dto.DeliveryInfoDto;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.order.dto.OrderDto;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.delivery.DriverDto;
import co.kurrant.app.admin_api.dto.delivery.ScheduleDto;
import co.kurrant.app.admin_api.mapper.DriverScheduleMapper;
import co.kurrant.app.admin_api.service.DriverService;
import exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final QDailyFoodRepository qDailyFoodRepository;
    private final QDriverScheduleRepository qDriverScheduleRepository;
    private final DriverScheduleMapper driverScheduleMapper;

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
    public void excelDriverSchedule(List<ScheduleDto> scheduleDtos) {

    }


}
