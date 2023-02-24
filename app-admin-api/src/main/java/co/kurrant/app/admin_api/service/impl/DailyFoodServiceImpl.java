package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.food.entity.*;
import co.dalicious.domain.food.mapper.CapacityMapper;
import co.dalicious.domain.food.mapper.DailyFoodMapper;
import co.dalicious.domain.food.repository.DailyFoodRepository;
import co.dalicious.domain.food.repository.FoodScheduleRepository;
import co.dalicious.domain.food.repository.MakersScheduleRepository;
import co.dalicious.domain.food.repository.QPresetDailyFoodRepository;
import co.dalicious.system.util.PeriodDto;
import co.kurrant.app.admin_api.service.DailyFoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DailyFoodServiceImpl implements DailyFoodService {
    private final QPresetDailyFoodRepository qPresetDailyFoodRepository;
    private final CapacityMapper capacityMapper;
    private final DailyFoodMapper dailyFoodMapper;
    private final MakersScheduleRepository makersScheduleRepository;
    private final FoodScheduleRepository foodScheduleRepository;
    private final DailyFoodRepository dailyFoodRepository;

    @Override
    @Transactional
    public void approveSchedule(PeriodDto.PeriodStringDto periodStringDto) {
        PeriodDto periodDto = periodStringDto.toPeriodDto();
        List<PresetDailyFood> presetDailyFoods = qPresetDailyFoodRepository.getApprovedPresetDailyFoodBetweenServiceDate(periodDto.getStartDate(), periodDto.getEndDate());

        Set<PresetMakersDailyFood> presetMakersDailyFoodSet = new HashSet<>();

        // 식단 저장 후 저장할 FoodSchedule을 찾은 후 저장한다.
        for (PresetDailyFood presetDailyFood : presetDailyFoods) {
            DailyFood dailyFood = dailyFoodMapper.toDailyFood(presetDailyFood);
            dailyFoodRepository.save(dailyFood);
            FoodSchedule foodSchedule = capacityMapper.toFoodSchedule(presetDailyFood);
            if(foodSchedule != null) {
                foodScheduleRepository.save(foodSchedule);
            }
            presetMakersDailyFoodSet.add(presetDailyFood.getPresetGroupDailyFood().getPresetMakersDailyFood());
        }

        // 저장할 MakersSchedule을 찾은 후 저장한다.
        for (PresetMakersDailyFood presetMakersDailyFood : presetMakersDailyFoodSet) {
            MakersSchedule makersSchedule = capacityMapper.toMakersSchedule(presetMakersDailyFood);
            if(makersSchedule != null) {
                makersScheduleRepository.save(makersSchedule);
            }
        }
    }

    @Override
    @Transactional
    public void getDailyFoods() {

    }
}
