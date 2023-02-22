package co.kurrant.app.admin_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.food.dto.PresetScheduleResponseDto;
import co.kurrant.app.admin_api.dto.schedules.ExcelPresetDailyFoodDto;

public interface ScheduleService {
    void makePresetSchedulesByExcel(ExcelPresetDailyFoodDto dtoList);
    ListItemResponseDto<PresetScheduleResponseDto> getAllPresetScheduleList(OffsetBasedPageRequest pageable, Integer size, Integer page);
}
