package co.kurrant.app.admin_api.service;

import co.dalicious.system.util.PeriodDto;
import co.kurrant.app.admin_api.dto.DeliveryDto;

import java.math.BigInteger;
import java.util.List;

public interface DeliveryService {
    DeliveryDto getDeliverySchedule(PeriodDto.PeriodStringDto periodDto, List<BigInteger> groupIds);
}
