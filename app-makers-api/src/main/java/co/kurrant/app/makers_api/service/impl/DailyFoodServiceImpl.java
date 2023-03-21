package co.kurrant.app.makers_api.service.impl;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.FoodSchedule;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.food.repository.QFoodScheduleRepository;
import co.dalicious.domain.user.repository.QUserGroupRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.makers_api.dto.DailyFoodDto;
import co.kurrant.app.makers_api.mapper.MakersDailyFoodMapper;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.DailyFoodService;
import co.kurrant.app.makers_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyFoodServiceImpl implements DailyFoodService {
    private final UserUtil userUtil;
    private final QDailyFoodRepository qDailyFoodRepository;
    private final QFoodScheduleRepository qFoodScheduleRepository;
    private final MakersDailyFoodMapper dailyFoodMapper;
    private final QUserGroupRepository qUserGroupRepository;
    @Override
    @Transactional(readOnly = true)
    public List<DailyFoodDto> getDailyFood(SecurityUser securityUser, Map<String, Object> parameter) {
        LocalDate startDate = !parameter.containsKey("startDate") || parameter.get("startDate") ==  null ? null : DateUtils.stringToDate(String.valueOf(parameter.get("startDate")));
        LocalDate endDate = !parameter.containsKey("endDate") || parameter.get("endDate") ==  null ? null : DateUtils.stringToDate(String.valueOf(parameter.get("endDate")));

        Makers makers = userUtil.getMakers(securityUser);

        // 만일 날짜를 보내주지 않으면 해당 일이 포함된 주차의 날을 구하기
        List<DailyFood> dailyFoodList;
        if(startDate == null && endDate == null) {
            LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
            Map<String, LocalDate> weekOfDay = DateUtils.getWeekOfDay(now);
            dailyFoodList = qDailyFoodRepository.findAllDailyFoodByMakersBetweenServiceDate(weekOfDay.get("startDate"), weekOfDay.get("endDate"), makers);
        }
        else{
            dailyFoodList = qDailyFoodRepository.findAllDailyFoodByMakersBetweenServiceDate(startDate, endDate, makers);
        }

        // 날짜에 해당하는 식단 가져오기
        List<FoodSchedule> foodScheduleList = qFoodScheduleRepository.findAllByDailyFoods(dailyFoodList);
        List<Group> groupList = dailyFoodList.stream().map(DailyFood::getGroup).toList();

        Map<Group,Integer> groupIntegerMap = qUserGroupRepository.userCountsInGroup(groupList);

        List<DailyFoodDto> dailyFoodDtoList = new ArrayList<>();
        if(dailyFoodList.isEmpty()) return dailyFoodDtoList;

        MultiValueMap<LocalDate, DailyFood> serviceDateMap = new LinkedMultiValueMap<>();
        for(DailyFood dailyFood : dailyFoodList) {
            LocalDate serviceDate = dailyFood.getServiceDate();
            serviceDateMap.add(serviceDate, dailyFood);
        }

        for(LocalDate serviceDate : serviceDateMap.keySet()) {
            List<DailyFood> serviceDateMapDailyFoodList = serviceDateMap.get(serviceDate);

            MultiValueMap<DiningType, DailyFood> diningTypeMap = new LinkedMultiValueMap<>();
            for(DailyFood dailyFood : Objects.requireNonNull(serviceDateMapDailyFoodList)) {
                DiningType diningType = dailyFood.getDiningType();
                diningTypeMap.add(diningType, dailyFood);
            }

            Integer groupCapacity = 0;
            FoodSchedule foodSchedule = null;
            List<DailyFoodDto.DailyFoodDining> dailyFoodDiningList = new ArrayList<>();
            for(DiningType diningType : diningTypeMap.keySet()) {
                List<DailyFood> diningTypeMapDailyFoodList = diningTypeMap.get(diningType);

                MultiValueMap<Food, DailyFood> foodMap = new LinkedMultiValueMap<>();
                for(DailyFood dailyFood : Objects.requireNonNull(diningTypeMapDailyFoodList)) {
                    Food food = dailyFood.getFood();
                    foodMap.add(food, dailyFood);

                    foodSchedule = foodScheduleList.stream()
                            .filter(schedule -> schedule.getFood().equals(dailyFood.getFood()) && schedule.getServiceDate().equals(dailyFood.getServiceDate()) && schedule.getDiningType().equals(dailyFood.getDiningType()))
                            .findFirst().orElse(null);

                    Group group = groupIntegerMap.keySet().stream().filter(g -> g.equals(dailyFood.getGroup()))
                            .findFirst()
                            .orElse(null);
                    if(group != null) {
                        groupCapacity += groupIntegerMap.get(group);
                    }
                }

                List<DailyFoodDto.DailyFood> dtoDailyFoodList = new ArrayList<>();
                for(Food food : foodMap.keySet()) {
                    DailyFoodDto.DailyFood dtoDailyFood = dailyFoodMapper.toDailyFood(food, foodSchedule, diningType);
                    dtoDailyFoodList.add(dtoDailyFood);
                }

                DailyFoodDto.DailyFoodDining dailyFoodDining = dailyFoodMapper.toDailyFoodDining(diningType, groupCapacity, dtoDailyFoodList);
                dailyFoodDiningList.add(dailyFoodDining);
            }
            dailyFoodDiningList = dailyFoodDiningList.stream().sorted(Comparator.comparing(DailyFoodDto.DailyFoodDining::getDiningType)).collect(Collectors.toList());

            DailyFoodDto dailyFoodDto = dailyFoodMapper.toDailyFoodDto(serviceDate, dailyFoodDiningList);
            dailyFoodDtoList.add(dailyFoodDto);
        }

        return dailyFoodDtoList;
    }
}
