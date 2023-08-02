package co.kurrant.app.public_api.service;

import co.dalicious.domain.user.dto.SaveDailyReportDto;
import co.dalicious.domain.user.dto.SaveDailyReportFoodReqDto;
import co.dalicious.domain.user.dto.SaveDailyReportReqDto;
import co.dalicious.domain.user.dto.UserPreferenceDto;
import co.kurrant.app.public_api.dto.board.PushResponseDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public interface DailyReportService {

    String userPreferenceSave(SecurityUser securityUser, UserPreferenceDto userPreferenceDto);

    Object getCountry();

    Object getFavoriteCountryFoods(Integer code);

    Object getJobType(Integer category, String code);

    Object getFoodImage(List<BigInteger> foodId);
    Object getTestData();

    Boolean userPreferenceCheck(SecurityUser securityUser);

    List<PushResponseDto> getAlarms(SecurityUser securityUser);
    void insertMyFood(SecurityUser securityUser, SaveDailyReportDto saveDailyReportDto);
    Object getReport(SecurityUser securityUser, String date);
    void saveDailyReportFood(SecurityUser securityUser, SaveDailyReportFoodReqDto dto);
    String deleteReport(SecurityUser securityUser, BigInteger reportId);
    Object getOrderByDateAndDiningType(SecurityUser securityUser, String date, Integer diningType);
    void allChangeAlarmSetting(SecurityUser securityUser, Boolean isActive);

    Object getMealHistory(SecurityUser securityUser, String startDate, String endDate);

    void saveDailyReport(SecurityUser securityUser, SaveDailyReportReqDto saveDailyReportDto);
}
