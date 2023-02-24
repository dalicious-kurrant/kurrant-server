package co.kurrant.app.admin_api.service;

import co.dalicious.system.util.PeriodDto;

public interface DailyFoodService {
    void approveSchedule(PeriodDto.PeriodStringDto periodStringDto);
    void getDailyFoods();
}
