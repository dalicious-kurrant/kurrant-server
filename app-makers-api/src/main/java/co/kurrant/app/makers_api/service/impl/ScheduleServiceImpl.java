package co.kurrant.app.makers_api.service.impl;

import co.dalicious.domain.food.dto.PresetScheduleRequestDto;
import co.dalicious.domain.food.dto.PresetScheduleResponseDto;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.PresetDailyFood;
import co.dalicious.domain.food.entity.PresetGroupDailyFood;
import co.dalicious.domain.food.entity.PresetMakersDailyFood;
import co.dalicious.domain.food.entity.enums.ScheduleStatus;
import co.dalicious.domain.food.mapper.PresetDailyFoodMapper;
import co.dalicious.domain.food.repository.PresetDailyFoodRepository;
import co.dalicious.domain.food.repository.PresetMakersDailyFoodRepository;
import co.dalicious.domain.food.repository.QPresetDailyFoodRepository;
import co.dalicious.domain.food.repository.QPresetMakersDailyFoodRepository;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.ScheduleService;
import co.kurrant.app.makers_api.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final UserUtil userUtil;
    private final QPresetMakersDailyFoodRepository qPresetGroupDailyFoodRepository;
    private final PresetDailyFoodMapper presetDailyFoodMapper;
    private final PresetMakersDailyFoodRepository presetMakersDailyFoodRepository;
    private final QPresetDailyFoodRepository qPresetDailyFoodRepository;
    private final PresetDailyFoodRepository presetDailyFoodRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PresetScheduleResponseDto> getMostRecentPresets(Integer page, SecurityUser securityUser) {
        Makers makers = userUtil.getMakers(securityUser);
        // 페이지에 해당하는 리스트만 불러옴
        List<PresetMakersDailyFood> presetMakersDailyFoods = qPresetGroupDailyFoodRepository.getMostRecentPresets(page, makers);

        //PresetScheduleResponseDto 만들기
        List<PresetScheduleResponseDto> responseDtos = new ArrayList<>();

        presetMakersDailyFoods.forEach(makersPreset -> {
            List<PresetGroupDailyFood> groupDailyFoods = makersPreset.getPresetGroupDailyFoods();
            List<PresetScheduleResponseDto.clientSchedule> clientSchedules = new ArrayList<>();

            groupDailyFoods.forEach(groupPreset -> {
                List<PresetDailyFood> presetDailyFoods = groupPreset.getPresetDailyFoods();
                List<PresetScheduleResponseDto.foodSchedule> foodSchedules = new ArrayList<>();
                //foodSchedules 만들기
                presetDailyFoods.forEach(dailyPreset -> {
                    PresetScheduleResponseDto.foodSchedule foodSchedule = presetDailyFoodMapper.toFoodScheduleDto(dailyPreset);
                    foodSchedules.add(foodSchedule);
                });
                //clientSchedules 만들기
                PresetScheduleResponseDto.clientSchedule clientSchedule = presetDailyFoodMapper.toClientScheduleDto(groupPreset, foodSchedules);
                clientSchedules.add(clientSchedule);
            });
            //response 할 DTO 만들기
            PresetScheduleResponseDto responseDto = presetDailyFoodMapper.toDto(makersPreset, clientSchedules);
            responseDtos.add(responseDto);
        });

        return responseDtos;
    }

    @Override
    @Transactional
    public void updateScheduleStatus(SecurityUser securityUser, PresetScheduleRequestDto requestDto) {
        Makers makers = userUtil.getMakers(securityUser);

        // 메이커스의 식단 타입과 서비스 일 별 예비 스케쥴 중 거절이 있으면 update
        List<PresetScheduleRequestDto.MakersScheduleDto> makersScheduleDtos = requestDto.getMakersScheduleDtos();
        makersScheduleDtos.forEach(makersSchedule -> {
            if(makersSchedule.getScheduleStatus() != 0){
                        //presetMakersDailyFood status 변경
                        PresetMakersDailyFood makersDailyFood = qPresetGroupDailyFoodRepository.findByIdAndMakers(makersSchedule.getPresetMakersId(), makers);
                        makersDailyFood.updateScheduleStatus(ScheduleStatus.ofCode(makersSchedule.getScheduleStatus()));
                        presetMakersDailyFoodRepository.save(makersDailyFood);
                    }
                }
        );

        // 메이커스의 식품별 예비 스케쥴 중 거절이 있으면 update
        List<PresetScheduleRequestDto.FoodScheduleDto> foodScheduleDtos = requestDto.getFoodScheduleDtos();
        foodScheduleDtos.forEach(foodSchedule ->{
            if(foodSchedule.getScheduleStatus() != 0) {
                PresetDailyFood presetDailyFood = qPresetDailyFoodRepository.findById(foodSchedule.getPresetFoodId());
                presetDailyFood.updateStatus(ScheduleStatus.ofCode(foodSchedule.getScheduleStatus()));
                presetDailyFoodRepository.save(presetDailyFood);
            }
        });

    }
}
