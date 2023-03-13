package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.system.util.PeriodDto;
import co.kurrant.app.admin_api.dto.DeliveryDto;
import co.kurrant.app.admin_api.mapper.DeliveryMapper;
import co.kurrant.app.admin_api.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    private final QGroupRepository qGroupRepository;
    private final DeliveryMapper deliveryMapper;

    @Override
    @Transactional(readOnly = true)
    public DeliveryDto getDeliverySchedule(PeriodDto.PeriodStringDto periodDto, List<BigInteger> groupIds) {
        List<Group> groups = (groupIds == null) ? null : qGroupRepository.findAllByIds(groupIds);
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findAllFilterGroup(periodDto.toPeriodDto().getStartDate(), periodDto.toPeriodDto().getEndDate(), groups);

//        List<DeliveryDto.DeliveryFood> deliveryFoodList = orderItemDailyFoods.stream().map(Group)

        return null;
    }
}
