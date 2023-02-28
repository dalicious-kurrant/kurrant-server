package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.food.entity.*;
import co.dalicious.domain.food.entity.enums.ConfirmStatus;
import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.domain.food.mapper.CapacityMapper;
import co.dalicious.domain.food.mapper.DailyFoodMapper;
import co.dalicious.domain.food.repository.*;
import co.dalicious.domain.order.dto.CapacityDto;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.domain.order.util.OrderDailyFoodUtil;
import co.dalicious.domain.user.repository.QUserGroupRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.PeriodDto;
import co.dalicious.system.util.StringUtils;
import co.dalicious.domain.food.dto.FoodDto;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.MakersDto;
import co.kurrant.app.admin_api.dto.ScheduleDto;
import co.kurrant.app.admin_api.mapper.GroupMapper;
import co.kurrant.app.admin_api.mapper.MakersMapper;
import co.kurrant.app.admin_api.mapper.ScheduleMapper;
import co.kurrant.app.admin_api.service.DailyFoodService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyFoodServiceImpl implements DailyFoodService {
    private final QPresetDailyFoodRepository qPresetDailyFoodRepository;
    private final CapacityMapper capacityMapper;
    private final DailyFoodMapper dailyFoodMapper;
    private final ScheduleMapper scheduleMapper;
    private final QGroupRepository qGroupRepository;
    private final MakersScheduleRepository makersScheduleRepository;
    private final FoodScheduleRepository foodScheduleRepository;
    private final DailyFoodRepository dailyFoodRepository;
    private final QDailyFoodRepository qDailyFoodRepository;
    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    private final QUserGroupRepository qUserGroupRepository;
    private final GroupRepository groupRepository;
    private final MakersRepository makersRepository;
    private final OrderDailyFoodUtil orderDailyFoodUtil;
    private final GroupMapper groupMapper;
    private final MakersMapper makersMapper;
    private final QMakersRepository qMakersRepository;
    private final QFoodRepository qFoodRepository;

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
            if (foodSchedule != null) {
                foodScheduleRepository.save(foodSchedule);
            }
            presetMakersDailyFoodSet.add(presetDailyFood.getPresetGroupDailyFood().getPresetMakersDailyFood());
        }

        // 저장할 MakersSchedule을 찾은 후 저장한다.
        for (PresetMakersDailyFood presetMakersDailyFood : presetMakersDailyFoodSet) {
            MakersSchedule makersSchedule = capacityMapper.toMakersSchedule(presetMakersDailyFood);
            if (makersSchedule != null) {
                makersScheduleRepository.save(makersSchedule);
            }
            presetMakersDailyFood.updateConfirmStatus(ConfirmStatus.COMPLETE);
        }
    }

    @Override
    @Transactional
    public List<ScheduleDto.GroupSchedule> getDailyFoods(Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));
        List<BigInteger> groupIds = !parameters.containsKey("groupIds") || parameters.get("groupIds").equals("") ? null : StringUtils.parseBigIntegerList((String) parameters.get("groupIds"));
        List<BigInteger> makersIds = !parameters.containsKey("makersIds") || parameters.get("makersIds").equals("") ? null : StringUtils.parseBigIntegerList((String) parameters.get("makersIds"));

        List<DailyFood> dailyFoods = qDailyFoodRepository.findAllByGroupAndMakersBetweenServiceDate(startDate, endDate, groupIds, makersIds);
        List<Group> groups = new ArrayList<>();
        for (DailyFood dailyFood : dailyFoods) {
            groups.add(dailyFood.getGroup());
        }
        Map<DailyFood, Integer> remainFoodCount = orderDailyFoodUtil.getRemainFoodsCount(dailyFoods);
        List<CapacityDto.MakersCapacity> makersCapacities = qOrderDailyFoodRepository.getMakersCounts(dailyFoods);
        Map<Group, Integer> userGroupCount = qUserGroupRepository.userCountsInGroup(groups);

        return scheduleMapper.toGroupSchedule(dailyFoods, remainFoodCount, makersCapacities, userGroupCount);
    }

    @Override
    public GroupDto.GroupAndMakers getGroupAndMakers() {
        List<Group> groups = groupRepository.findAll();
        List<Makers> makers = makersRepository.findAll();

        List<GroupDto.Group> groupDtos = groupMapper.groupsToDtos(groups);
        List<MakersDto.Makers> makersDtos = makersMapper.makersToDtos(makers);

        GroupDto.GroupAndMakers groupAndMakers = new GroupDto.GroupAndMakers();
        groupAndMakers.setGroups(groupDtos);
        groupAndMakers.setMakers(makersDtos);

        return groupAndMakers;
    }

    @Override
    @Transactional
    public void excelDailyFoods(List<FoodDto.DailyFood> dailyFoodList) {
        // 업데이트할 식단
        List<BigInteger> dailyFoodIds = dailyFoodList.stream()
                .map(FoodDto.DailyFood::getDailyFoodId)
                .toList();
        List<DailyFood> dailyFoods = qDailyFoodRepository.findAllByDailyFoodIds(dailyFoodIds);

        List<BigInteger> currentDailyFoodIds = dailyFoods.stream()
                .map(DailyFood::getId)
                .toList();

        Set<String> updateMakersNames = dailyFoodList.stream()
                .filter(dailyFood -> currentDailyFoodIds.contains(dailyFood.getDailyFoodId()))
                .map(FoodDto.DailyFood::getMakersName)
                .collect(Collectors.toSet());
        Set<String> updateGroupNames = dailyFoodList.stream()
                .filter(dailyFood -> currentDailyFoodIds.contains(dailyFood.getDailyFoodId()))
                .map(FoodDto.DailyFood::getGroupName)
                .collect(Collectors.toSet());

        List<Makers> updateMakersList = qMakersRepository.getMakersByName(updateMakersNames);
        List<Food> updateFoods = qFoodRepository.findByMakers(updateMakersList);
        List<Group> updateGroups = qGroupRepository.findAllByNames(updateGroupNames);

        dailyFoods.forEach(dailyFood -> {
            FoodDto.DailyFood dailyFoodDto = dailyFoodList.stream()
                    .filter(v -> v.getDailyFoodId().equals(dailyFood.getId()))
                    .findAny()
                    .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
            dailyFood.updateFoodStatus(DailyFoodStatus.ofCode(dailyFoodDto.getFoodStatus()));
            if(dailyFoodDto.getFoodCapacity().equals(dailyFoodDto.getFoodCount())) {
                dailyFood.updateDiningType(DiningType.ofCode(dailyFoodDto.getDiningType()));
                dailyFood.updateServiceDate(DateUtils.stringToDate(dailyFoodDto.getServiceDate()));
                dailyFood.updateFood(Food.getFood(updateFoods, dailyFoodDto.getMakersName(), dailyFoodDto.getFoodName()));
                dailyFood.updateGroup(Group.getGroup(updateGroups, dailyFoodDto.getGroupName()));
            }
        });

        List<BigInteger> newDailyFoodIds = dailyFoodList.stream()
                .map(FoodDto.DailyFood::getDailyFoodId)
                .filter(dailyFoodId -> !currentDailyFoodIds.contains(dailyFoodId))
                .toList();
        List<FoodDto.DailyFood> newDailyFoodDtos = dailyFoodList.stream()
                .filter(dailyFood -> newDailyFoodIds.contains(dailyFood.getDailyFoodId()))
                .toList();
        Set<String> makersName = newDailyFoodDtos.stream()
                .map(FoodDto.DailyFood::getMakersName)
                .collect(Collectors.toSet());
        Set<String> groupNames = newDailyFoodDtos.stream()
                .map(FoodDto.DailyFood::getGroupName)
                .collect(Collectors.toSet());

        List<Makers> makersList = qMakersRepository.getMakersByName(makersName);
        List<Food> foodsByMakers = qFoodRepository.findByMakers(makersList);
        List<Group> groups = qGroupRepository.findAllByNames(groupNames);
        List<DailyFood> newDailyFoods = dailyFoodMapper.toDailyFoods(newDailyFoodDtos, groups, foodsByMakers);
        dailyFoodRepository.saveAll(newDailyFoods);
    }
}
