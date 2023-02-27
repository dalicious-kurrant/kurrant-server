package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.food.entity.*;
import co.dalicious.domain.food.entity.enums.ConfirmStatus;
import co.dalicious.domain.food.mapper.CapacityMapper;
import co.dalicious.domain.food.mapper.DailyFoodMapper;
import co.dalicious.domain.food.repository.*;
import co.dalicious.domain.order.dto.CapacityDto;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.domain.order.util.OrderDailyFoodUtil;
import co.dalicious.domain.user.repository.QUserGroupRepository;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.PeriodDto;
import co.dalicious.system.util.StringUtils;
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

@Service
@RequiredArgsConstructor
public class DailyFoodServiceImpl implements DailyFoodService {
    private final QPresetDailyFoodRepository qPresetDailyFoodRepository;
    private final CapacityMapper capacityMapper;
    private final DailyFoodMapper dailyFoodMapper;
    private final ScheduleMapper scheduleMapper;
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
}
