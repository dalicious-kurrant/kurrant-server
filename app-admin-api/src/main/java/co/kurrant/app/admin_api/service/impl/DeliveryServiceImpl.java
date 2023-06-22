package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.client.repository.SpotRepository;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.delivery.repository.QDeliveryInstanceRepository;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.MakersDto;
import co.kurrant.app.admin_api.dto.delivery.DeliveryDto;
import co.kurrant.app.admin_api.mapper.DeliveryMapper;
import co.kurrant.app.admin_api.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryMapper deliveryMapper;
    private final QDailyFoodRepository qDailyFoodRepository;
    private final QDeliveryInstanceRepository qDeliveryInstanceRepository;
    private final QGroupRepository qGroupRepository;

    @Override
    @Transactional(readOnly = true)
    public DeliveryDto getDeliverySchedule(String start, String end, List<BigInteger> groupIds, List<BigInteger> spotIds, Integer isAll) {
        LocalDate startDate = (start == null) ? null : DateUtils.stringToDate(start);
        LocalDate endDate = (end == null) ? null : DateUtils.stringToDate(end);

        List<Group> groupAllList = qGroupRepository.findAllExceptForMySpot();
        List<Group> groups = (groupIds == null) ? null : groupAllList.stream().filter(group -> groupIds.contains(group.getId())).toList();
        // 그룹과 연관된 스팟만 보여주기
        List<Spot> spotAllList = groups == null || groups.isEmpty() ? groupAllList.stream().flatMap(group -> group.getSpots().stream()).toList() : groups.stream().flatMap(group -> group.getSpots().stream()).toList();
        List<Spot> spots = (spotIds == null) ? null : spotAllList.stream().filter(spot -> spotIds.contains(spot.getId())).toList();

        List<DailyFood> dailyFoodList = qDailyFoodRepository.findAllFilterGroupAndSpot(startDate, endDate, groups, spots);
        List<DeliveryInstance> deliveryInstanceList = qDeliveryInstanceRepository.findByDailyFoodAndOrderStatus(dailyFoodList);

        List<DeliveryDto.DeliveryInfo> deliveryInfoList = new ArrayList<>();
        if(deliveryInstanceList.isEmpty()) return DeliveryDto.create(groupAllList, deliveryInfoList, spotAllList);
        return DeliveryDto.create(groupAllList, deliveryMapper.getDeliveryInfoList(deliveryInstanceList), spotAllList);
    }

    @Override
    public List<DeliveryDto.DeliveryManifest> getDeliveryManifest(Map<String, Object> parameters) {
        return null;
    }

    @Override
    public List<MakersDto.Makers> getDeliverMakersByDate(Map<String, Object> parameters) {
        return null;
    }

    @Override
    public List<String> getDeliveryTimesByDate(Map<String, Object> parameters) {
        return null;
    }

    @Override
    public List<String> getDeliveryCodesByDate(Map<String, Object> parameters) {
        return null;
    }

    @Override
    public List<GroupDto.User> getDeliverUsersByDate(Map<String, Object> parameters) {
        return null;
    }
}
