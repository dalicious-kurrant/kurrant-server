package co.kurrant.app.admin_api.service;

import co.dalicious.domain.food.dto.PresetScheduleDto;

import java.util.List;

public interface ScheduleService {
    public void makePresetSchedules(List<PresetScheduleDto> dataList);
}
