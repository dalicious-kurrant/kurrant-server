package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.alarm.dto.PushRequestDtoByUser;
import co.dalicious.client.alarm.entity.PushAlarms;
import co.dalicious.client.alarm.entity.enums.AlarmType;
import co.dalicious.client.alarm.repository.QPushAlarmsRepository;
import co.dalicious.client.alarm.service.PushService;
import co.dalicious.client.alarm.util.PushUtil;
import co.dalicious.client.sse.SseService;
import co.dalicious.data.redis.entity.PushAlarmHash;
import co.dalicious.data.redis.repository.PushAlarmHashRepository;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.delivery.repository.QDeliveryInstanceRepository;
import co.dalicious.domain.food.entity.embebbed.DeliverySchedule;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.food.dto.DailyFoodGroupDto;
import co.dalicious.domain.food.entity.*;
import co.dalicious.domain.food.entity.enums.ConfirmStatus;
import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.domain.food.mapper.CapacityMapper;
import co.dalicious.domain.food.mapper.DailyFoodMapper;
import co.dalicious.domain.food.repository.*;
import co.dalicious.domain.food.util.FoodUtils;
import co.dalicious.domain.order.dto.ServiceDateBy;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.domain.order.util.OrderDailyFoodUtil;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.PushCondition;
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
import exception.CustomException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
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
    private final DailyFoodGroupRepository dailyFoodGroupRepository;
    private final PushUtil pushUtil;
    private final PushService pushService;
    private final QPushAlarmsRepository qPushAlarmsRepository;
    private final PushAlarmHashRepository pushAlarmHashRepository;
    private final SseService sseService;
    private final FoodCapacityRepository foodCapacityRepository;
    private final QDeliveryInstanceRepository qDeliveryInstanceRepository;
    @Override
    @Transactional
    public void approveSchedule(PeriodDto.PeriodStringDto periodStringDto) {
        PeriodDto periodDto = periodStringDto.toPeriodDto();
        List<PresetDailyFood> presetDailyFoods = qPresetDailyFoodRepository.getApprovedPresetDailyFoodBetweenServiceDate(periodDto.getStartDate(), periodDto.getEndDate());

        Map<PresetGroupDailyFood, DailyFoodGroup> presetGroupDailyFoodMap = new HashMap<>();
        Set<PresetMakersDailyFood> presetMakersDailyFoodSet = new HashSet<>();

        // 푸시 알림을 보낼 그룹
        Set<BigInteger> groupIdSet = new HashSet<>();

        // DailyFoodGroup 저장
        for (PresetDailyFood presetDailyFood : presetDailyFoods) {
            presetGroupDailyFoodMap.put(presetDailyFood.getPresetGroupDailyFood(), dailyFoodGroupRepository.save(dailyFoodMapper.toDailyFoodGroup(presetDailyFood.getPresetGroupDailyFood())));
            groupIdSet.add(presetDailyFood.getPresetGroupDailyFood().getGroup().getId());
        }


        // 식단 저장 후 저장할 FoodSchedule을 찾은 후 저장한다.
        for (PresetDailyFood presetDailyFood : presetDailyFoods) {
            DailyFoodGroup dailyFoodGroup = presetGroupDailyFoodMap.get(presetDailyFood.getPresetGroupDailyFood());
            DailyFood dailyFood = dailyFoodMapper.toDailyFood(presetDailyFood, dailyFoodGroup);
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

        // TODO: 메이커스 승인 완료 하면 Push 알림 구현
        List<PushRequestDtoByUser> pushRequestDtoByUsers = new ArrayList<>();
        List<PushAlarmHash> pushAlarmHashes = new ArrayList<>();
         Map<User, Group> userGroupMap = qUserGroupRepository.findUserGroupFirebaseToken(groupIdSet);
        PushAlarms pushAlarms = qPushAlarmsRepository.findByPushCondition(PushCondition.NEW_DAILYFOOD);
        for (User user : userGroupMap.keySet()) {
            String message = PushUtil.getContextNewDailyFood(pushAlarms.getMessage(), userGroupMap.get(user).getName(), periodDto.getStartDate(), periodDto.getEndDate());
            PushRequestDtoByUser pushRequestDtoByUser = pushUtil.getPushRequest(user, PushCondition.NEW_DAILYFOOD, message);
            if (pushRequestDtoByUser != null) {
                pushRequestDtoByUsers.add(pushRequestDtoByUser);
            }

            PushAlarmHash pushAlarmHash = PushAlarmHash.builder()
                    .title(PushCondition.NEW_DAILYFOOD.getTitle())
                    .isRead(false)
                    .message(message)
                    .userId(user.getId())
                    .type(AlarmType.MEAL.getAlarmType())
                    .build();
            pushAlarmHashes.add(pushAlarmHash);

            sseService.send(user.getId(), 6, null, null, null);
        }
        pushService.sendToPush(pushRequestDtoByUsers);
        pushAlarmHashRepository.saveAll(pushAlarmHashes);
    }

    @Override
    @Transactional
    public List<ScheduleDto.GroupSchedule> getDailyFoods(Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));
        List<BigInteger> groupIds = !parameters.containsKey("groupIds") || parameters.get("groupIds").equals("") ? null : StringUtils.parseBigIntegerList((String) parameters.get("groupIds"));
        List<BigInteger> makersIds = !parameters.containsKey("makersIds") || parameters.get("makersIds").equals("") ? null : StringUtils.parseBigIntegerList((String) parameters.get("makersIds"));

        List<DailyFood> dailyFoods = qDailyFoodRepository.findAllByGroupAndMakersBetweenServiceDate(startDate, endDate, groupIds, makersIds);

        // 일치하는 식단이 없을 경우에는 빈 배열 return
        if (dailyFoods.isEmpty()) {
            return new ArrayList<>();
        }

        List<Group> groups = new ArrayList<>();
        for (DailyFood dailyFood : dailyFoods) {
            groups.add(dailyFood.getGroup());
        }
        ServiceDateBy.MakersAndFood makersOrderCount = qOrderDailyFoodRepository.getMakersCounts(dailyFoods);
        ServiceDateBy.MakersAndFood makersCapacities = orderDailyFoodUtil.getMakersCapacity(dailyFoods, makersOrderCount);
        Map<DailyFood, Integer> remainFoodCount = orderDailyFoodUtil.getRemainFoodsCount(dailyFoods);
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

        // Request 중, 해당하는 id를 가지고 있는 DailyFood 가져오기
        List<DailyFood> dailyFoods = new ArrayList<>();
        if (!dailyFoodIds.stream().allMatch(Objects::isNull)) {
            dailyFoods = qDailyFoodRepository.findAllByDailyFoodIds(dailyFoodIds);
        }

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

        // FIXME: DailyFoodGroup의 재정의. 제대로 사용하지 못하고 있음.
        MultiValueMap<DailyFoodGroupDto, FoodDto.DailyFood> dailyFoodGroupMap = new LinkedMultiValueMap<>();

        for (FoodDto.DailyFood dailyFood : dailyFoodList) {
            DailyFoodGroupDto dailyFoodGroupDto = new DailyFoodGroupDto(dailyFood);
            dailyFoodGroupMap.add(dailyFoodGroupDto, dailyFood);
        }

        for (DailyFoodGroupDto dailyFoodGroupDto : dailyFoodGroupMap.keySet()) {
            List<FoodDto.DailyFood> sortedDailyFoodDto = dailyFoodGroupMap.get(dailyFoodGroupDto);
            List<List<String>> makersPickupTimes = sortedDailyFoodDto.stream()
                    .map(FoodDto.DailyFood::getMakersPickupTime)
                    .toList();
            for (int i = 0; i < sortedDailyFoodDto.size(); i++ ) {
                if(sortedDailyFoodDto.get(i).getMakersPickupTime().size() != makersPickupTimes.get(i).size()){
                    throw new CustomException(HttpStatus.BAD_REQUEST, "CE4000019", dailyFoodGroupDto.getGroupName() + "스팟의 " + dailyFoodGroupDto.getMakersName() + " 상품별 픽업시간이 동일 하지 않습니다.");
                }
            }
        }

        List<FoodCapacity> newFoodCapacities = new ArrayList<>();
        MultiValueMap<Group, DailyFood> groupMap = new LinkedMultiValueMap<>();
        dailyFoods.forEach(dailyFood -> {
            FoodDto.DailyFood dailyFoodDto = dailyFoodList.stream()
                    .filter(v -> v.getDailyFoodId() != null && v.getDailyFoodId().equals(dailyFood.getId()))
                    .findAny()
                    .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
            Group group = Group.getGroup(updateGroups, dailyFoodDto.getGroupName());
            // 그룹이 가지고 있지 않은 식사 타입을 추가할 경우
            DiningType diningType = DiningType.ofCode(dailyFoodDto.getDiningType());
            if (!group.getDiningTypes().contains(diningType)) {
                throw new ApiException(ExceptionEnum.GROUP_DOSE_NOT_HAVE_DINING_TYPE);
            }
            // 메이커스/음식 주문 가능 수량이 존재하지 않을 경우
            if(dailyFood.getFood().getMakers().getMakersCapacity(diningType) == null) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "CE4000018", dailyFood.getFood().getMakers().getId() + "번 메이커스의 " + dailyFood.getDiningType().getDiningType() + " 주문 가능 수량이 존재하지 않습니다.");
            }
            if(dailyFood.getFood().getFoodCapacity(diningType) == null) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "CE4000018", dailyFood.getFood().getId() + "번 음식의 " + dailyFood.getDiningType().getDiningType() + " 주문 가능 수량이 존재하지 않습니다.");
            }
            // 푸시 알림 전송을 위해 등록대기였던 식단 추가
            DailyFood waitingDailyFood = null;
            if(dailyFood.getDailyFoodStatus().equals(DailyFoodStatus.WAITING_SALE)) {
                waitingDailyFood = dailyFood;
            }
            dailyFood.updateFoodStatus(DailyFoodStatus.ofCode(dailyFoodDto.getFoodStatus()));
            if(!DailyFoodStatus.ofCode(dailyFoodDto.getFoodStatus()).equals(DailyFoodStatus.SALES)) {
                waitingDailyFood = null;
            }

            List<String> groupDeliveryTimes = dailyFood.getGroup().getMealInfo(dailyFood.getDiningType()).getDeliveryTimes().stream().map(DateUtils::timeToString).toList();
            // 그룹이 가진 배송시간과 다른 배송시간을 요청한 경우
            if(dailyFoodDto.getDeliveryTime().stream().anyMatch(v -> !groupDeliveryTimes.contains(v))){
                throw new CustomException(
                        HttpStatus.BAD_REQUEST,
                        "CE4000020",
                        dailyFoodDto.getGroupName() + "스팟에서 지원하지 않는 배송시간입니다." + StringUtils.StringListToString(dailyFoodDto.getDeliveryTime()) + " -> " + StringUtils.StringListToString(groupDeliveryTimes));
            }
            dailyFoodMapper.updateDeliverySchedule(dailyFoodDto.getDeliveryTime(), dailyFoodDto.getMakersPickupTime(), dailyFood.getDailyFoodGroup());

            Food food = Food.getFood(updateFoods, dailyFoodDto.getMakersName(), dailyFoodDto.getFoodName());
            FoodCapacity foodCapacity = food.getFoodCapacity(DiningType.ofCode(dailyFoodDto.getDiningType()));

            // 수량이 기존과 다르거나 메이커스 수량과 다르면
            if (dailyFoodDto.getMakersCapacity().equals(dailyFoodDto.getFoodCapacity()) && foodCapacity == null) {
                newFoodCapacities.add(FoodCapacity.builder().food(food).capacity(dailyFoodDto.getFoodCapacity()).diningType(DiningType.ofCode(dailyFoodDto.getDiningType())).build());
            } else if (!Objects.equals(foodCapacity.getCapacity(), dailyFoodDto.getFoodCapacity())) {
                foodCapacity.updateCapacity(dailyFoodDto.getFoodCapacity());
            }

            // 식단을 구매한 사람이 없다면
            if (dailyFoodDto.getFoodCapacity().equals(dailyFoodDto.getFoodCount())) {
                dailyFood.updateDiningType(DiningType.ofCode(dailyFoodDto.getDiningType()));
                dailyFood.updateServiceDate(DateUtils.stringToDate(dailyFoodDto.getServiceDate()));
                dailyFood.updateFood(food);
                dailyFood.updateDailyFoodPrice(food);
                dailyFood.updateGroup(group);
            }
            if(waitingDailyFood != null) {
                groupMap.add(waitingDailyFood.getGroup(),waitingDailyFood);
            }
        });
        foodCapacityRepository.saveAll(newFoodCapacities);

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

        // 픽업 시간 저장
        MultiValueMap<DailyFoodGroup, FoodDto.DailyFood> newDailyFoodGroupMap = new LinkedMultiValueMap<>();
        MultiValueMap<DailyFoodGroupDto, FoodDto.DailyFood> dailyFoodGroupDtoMap = new LinkedMultiValueMap<>();
        for (FoodDto.DailyFood dailyFood : newDailyFoodDtos) {
            dailyFoodGroupDtoMap.add(new DailyFoodGroupDto(dailyFood), dailyFood);
        }

        for (DailyFoodGroupDto dailyFoodGroupDto : dailyFoodGroupDtoMap.keySet()) {
            List<FoodDto.DailyFood> dailyFoodDtos = dailyFoodGroupDtoMap.get(dailyFoodGroupDto);

            Map<String, String> deliveryScheduleMap = new HashMap<>();
            for(FoodDto.DailyFood dailyFood : Objects.requireNonNull(dailyFoodDtos)) {
                List<String> deliveryTimeList = dailyFood.getDeliveryTime();
                List<String> makersPickupTimeList = dailyFood.getMakersPickupTime();
                DiningType diningType = DiningType.ofCode(dailyFood.getDiningType());

                if(dailyFood.getDeliveryTime().size() != dailyFood.getMakersPickupTime().size()) throw new ApiException(ExceptionEnum.EXCEL_TIME_LIST_NOT_EQUAL);

                Makers makers = makersList.stream().filter(v -> v.getName().equals(dailyFood.getMakersName())).findAny()
                        .orElse(null);
                MealInfo mealInfo = groups.stream().filter(v -> v.getName().equals(dailyFood.getGroupName()))
                        .map(v -> v.getMealInfo(diningType))
                        .findAny().orElse(null);

                if(deliveryTimeList.stream().anyMatch(v -> Objects.requireNonNull(mealInfo).getDeliveryTimes().contains(DateUtils.stringToLocalTime(v)))) {
                    throw new CustomException(HttpStatus.BAD_REQUEST, "CE4000020", dailyFood.getGroupName() + "스팟에서 지원하지 않는 배송시간입니다.");
                }

                if(makers != null && dailyFood.getMakersPickupTime().size() == dailyFood.getDeliveryTime().size()) {
                    for (int i = 0; i < deliveryTimeList.size(); i++) {
                        String deliveryTime = deliveryTimeList.get(i);
                        LocalTime deliveryLocalTime = DateUtils.stringToLocalTime(deliveryTime);

                        if(FoodUtils.isValidDeliveryTime(makers, diningType, deliveryLocalTime) && Objects.requireNonNull(mealInfo).getDeliveryTimes().contains(deliveryLocalTime)) {
                            deliveryScheduleMap.put(deliveryTime, makersPickupTimeList.get(i));
                        }
                    }
                }
            }

            DailyFoodGroup dailyFoodGroup = dailyFoodGroupRepository.save(dailyFoodMapper.toDailyFoodGroup(deliveryScheduleMap));
            newDailyFoodGroupMap.put(dailyFoodGroup, dailyFoodDtos);
        }

        List<DailyFood> newDailyFoods = dailyFoodMapper.toDailyFoods(newDailyFoodGroupMap, groups, foodsByMakers);
        newDailyFoods = dailyFoodRepository.saveAll(newDailyFoods);

        // 식단이 생성 됐을 때 푸시알림
        Map<Group, Optional<LocalDate>> earliestDatesByGroup = groupMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().stream().min(Comparator.comparing(DailyFood::getServiceDate)).map(DailyFood::getServiceDate)));

        Map<Group, Optional<LocalDate>> latestDatesByGroup = groupMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().stream().max(Comparator.comparing(DailyFood::getServiceDate)).map(DailyFood::getServiceDate)));


        // 등록대기 -> 판매중으로 변경된 식단들만 푸시 알림 보내기
        List<PushRequestDtoByUser> pushRequestDtoByUsers = new ArrayList<>();
        List<PushAlarmHash> pushAlarmHashes = new ArrayList<>();
        Map<User, Group> userGroupMap = qUserGroupRepository.findUserGroupFirebaseTokenByGroup(groupMap.keySet());
        PushAlarms pushAlarms = qPushAlarmsRepository.findByPushCondition(PushCondition.NEW_DAILYFOOD);
        for (User user : userGroupMap.keySet()) {
            if (!earliestDatesByGroup.isEmpty() && !latestDatesByGroup.isEmpty() &&
                    earliestDatesByGroup.get(userGroupMap.get(user)).isPresent() &&
                    latestDatesByGroup.get(userGroupMap.get(user)).isPresent()) {
                String message = PushUtil.getContextNewDailyFood(pushAlarms.getMessage(), userGroupMap.get(user).getName(), earliestDatesByGroup.get(userGroupMap.get(user)).get(), latestDatesByGroup.get(userGroupMap.get(user)).get());
                PushRequestDtoByUser pushRequestDtoByUser = pushUtil.getPushRequest(user, PushCondition.NEW_DAILYFOOD, message);
                if (pushRequestDtoByUser != null) {
                    pushRequestDtoByUsers.add(pushRequestDtoByUser);
                }
                PushAlarmHash pushAlarmHash = PushAlarmHash.builder()
                        .title(PushCondition.NEW_DAILYFOOD.getTitle())
                        .isRead(false)
                        .message(message)
                        .userId(user.getId())
                        .type(AlarmType.MEAL.getAlarmType())
                        .build();
                pushAlarmHashes.add(pushAlarmHash);
                sseService.send(user.getId(), 6, null, null, null);
            }
        }
        pushService.sendToPush(pushRequestDtoByUsers);
        pushAlarmHashRepository.saveAll(pushAlarmHashes);
    }
}
