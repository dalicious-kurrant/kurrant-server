package co.kurrant.app.admin_api.service;

import co.dalicious.domain.food.dto.PresetScheduleDto;
import co.kurrant.app.admin_api.dto.ExcelPresetDailyFoodDto;

import java.util.List;

public interface ScheduleService {
    public void makePresetSchedulesByExcel(ExcelPresetDailyFoodDto dtoList);
}
