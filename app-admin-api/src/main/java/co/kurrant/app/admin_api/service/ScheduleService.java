package co.kurrant.app.admin_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.kurrant.app.admin_api.dto.schedules.ExcelPresetDailyFoodDto;

public interface ScheduleService {
    void makePresetSchedulesByExcel(ExcelPresetDailyFoodDto dtoList);
    void getAllPresetScheduleList(OffsetBasedPageRequest pageable);
}
