package co.kurrant.app.admin_api.service;

import co.kurrant.app.admin_api.dto.Code;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.MakersDto;
import co.kurrant.app.admin_api.dto.delivery.DeliveryStatusVo;
import co.kurrant.app.admin_api.dto.delivery.DeliveryVo;
import co.kurrant.app.admin_api.dto.user.LoginResponseDto;
import co.kurrant.app.admin_api.model.SecurityUser;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface DeliveryService {
    LoginResponseDto login(Code code);
    DeliveryVo getDelivery(String startDate, String endDate, List<BigInteger> groupIds, List<BigInteger> spotIds, Integer isAll);
    DeliveryVo getDeliverySchedule(SecurityUser driver, String startDate, String endDate, List<BigInteger> groupIds, List<BigInteger> spotIds, Integer isAll);
    List<DeliveryVo.DeliveryManifest> getDeliveryManifest(Map<String, Object> parameters);
    Integer requestDeliveryComplete(SecurityUser securityUser, DeliveryStatusVo deliveryStatusVo);
    void cancelDeliveryComplete(SecurityUser securityUser, DeliveryStatusVo deliveryStatusVo);
    List<MakersDto.Makers> getDeliverMakersByDate(Map<String, Object> parameters);
    List<String> getDeliveryTimesByDate(Map<String, Object> parameters);
    List<String> getDeliveryCodesByDate(Map<String, Object> parameters);
    List<GroupDto.User> getDeliverUsersByDate(Map<String, Object> parameters);
}
