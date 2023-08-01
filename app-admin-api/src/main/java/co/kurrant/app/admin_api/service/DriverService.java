package co.kurrant.app.admin_api.service;

import co.dalicious.domain.delivery.dto.DeliveryInstanceDto;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.delivery.entity.Driver;
import co.dalicious.domain.order.dto.OrderDto;
import co.kurrant.app.admin_api.dto.delivery.DriverDto;
import co.kurrant.app.admin_api.dto.delivery.ScheduleDto;

import java.util.List;
import java.util.Map;

public interface DriverService {
    List<Driver> getDrivers();
    void postDrivers(List<DriverDto> driverDtos);
    void deleteDrivers(OrderDto.IdList idList);
    List<DeliveryInstanceDto> getDriverSchedule(Map<String, Object> parameters);
    void postDriverSchedule(List<DeliveryInstanceDto> deliveryInstances);
}
