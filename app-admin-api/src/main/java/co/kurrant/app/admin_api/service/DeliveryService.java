package co.kurrant.app.admin_api.service;

import co.kurrant.app.admin_api.dto.delivery.DeliveryDto;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface DeliveryService {
    DeliveryDto getDeliverySchedule(String startDate, String endDate, List<BigInteger> groupIds, List<BigInteger> spotIds, Integer isAll);
    List<DeliveryDto.DeliveryManifest> getDeliveryManifest(Map<String, Object> parameters);
}
