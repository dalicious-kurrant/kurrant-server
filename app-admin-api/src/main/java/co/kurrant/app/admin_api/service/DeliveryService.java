package co.kurrant.app.admin_api.service;

import co.dalicious.domain.user.dto.UserDto;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.MakersDto;
import co.kurrant.app.admin_api.dto.delivery.DeliveryDto;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface DeliveryService {
    DeliveryDto getDeliverySchedule(String startDate, String endDate, List<BigInteger> groupIds, List<BigInteger> spotIds, Integer isAll);
    List<DeliveryDto.DeliveryManifest> getDeliveryManifest(Map<String, Object> parameters);

    List<MakersDto.Makers> getDeliverMakersByDate(Map<String, Object> parameters);
    List<String> getDeliveryTimesByDate(Map<String, Object> parameters);
    List<String> getDeliveryCodesByDate(Map<String, Object> parameters);
    List<GroupDto.User> getDeliverUsersByDate(Map<String, Object> parameters);
}
