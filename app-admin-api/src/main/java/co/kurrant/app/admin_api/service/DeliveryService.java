package co.kurrant.app.admin_api.service;

import co.dalicious.domain.user.dto.UserDto;
import co.kurrant.app.admin_api.dto.Code;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.MakersDto;
import co.kurrant.app.admin_api.dto.delivery.DeliveryDto;
import co.kurrant.app.admin_api.dto.user.LoginResponseDto;
import co.kurrant.app.admin_api.model.SecurityUser;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface DeliveryService {
    LoginResponseDto login(Code code) throws IOException;
    DeliveryDto getDelivery(String startDate, String endDate, List<BigInteger> groupIds, List<BigInteger> spotIds, Integer isAll);
    DeliveryDto getDeliverySchedule(SecurityUser driver, String startDate, String endDate, List<BigInteger> groupIds, List<BigInteger> spotIds, Integer isAll);
    List<DeliveryDto.DeliveryManifest> getDeliveryManifest(Map<String, Object> parameters);
    List<MakersDto.Makers> getDeliverMakersByDate(Map<String, Object> parameters);
    List<String> getDeliveryTimesByDate(Map<String, Object> parameters);
    List<String> getDeliveryCodesByDate(Map<String, Object> parameters);
    List<GroupDto.User> getDeliverUsersByDate(Map<String, Object> parameters);
}
