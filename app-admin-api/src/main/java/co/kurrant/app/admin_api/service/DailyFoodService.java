package co.kurrant.app.admin_api.service;

import co.dalicious.system.util.PeriodDto;
import co.kurrant.app.admin_api.dto.ScheduleDto;

import java.util.List;
import java.util.Map;

public interface DailyFoodService {
    void approveSchedule(PeriodDto.PeriodStringDto periodStringDto);
    List<ScheduleDto.GroupSchedule> getDailyFoods(Map<String, Object> parameters);
}
