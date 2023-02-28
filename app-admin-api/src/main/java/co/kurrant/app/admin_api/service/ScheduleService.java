package co.kurrant.app.admin_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.kurrant.app.admin_api.dto.schedules.ExcelPresetDailyFoodDto;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.kurrant.app.admin_api.dto.schedules.ScheduleResponseDto;

import java.util.Map;

public interface ScheduleService {
    void makePresetSchedulesByExcel(ExcelPresetDailyFoodDto dtoList);
    ItemPageableResponseDto<ScheduleResponseDto> getAllPresetScheduleList(Map<String, Object> parameters, OffsetBasedPageRequest pageable, Integer size, Integer page);
    ItemPageableResponseDto<ScheduleResponseDto> getRecommendPresetSchedule(String startDate, String endDate, OffsetBasedPageRequest pageable, Integer size, Integer page);
    void updateDataInTemporary(ExcelPresetDailyFoodDto dtoList);
}
