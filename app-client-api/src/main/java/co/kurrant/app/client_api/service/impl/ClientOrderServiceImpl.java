package co.kurrant.app.client_api.service.impl;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.MakersRepository;
import co.dalicious.domain.order.dto.OrderDto;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.PaymentCancelHistory;
import co.dalicious.domain.order.mapper.OrderMapper;
import co.dalicious.domain.order.repository.OrderRepository;
import co.dalicious.domain.order.repository.PaymentCancelHistoryRepository;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.repository.UserGroupRepository;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.StringUtils;
import co.dalicious.domain.order.dto.GroupDto;
import co.kurrant.app.client_api.mapper.GroupMapper;
import co.kurrant.app.client_api.model.SecurityUser;
import co.kurrant.app.client_api.service.ClientOrderService;
import co.kurrant.app.client_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientOrderServiceImpl implements ClientOrderService {
    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    private final UserUtil userUtil;
    private final OrderMapper orderMapper;
    private final PaymentCancelHistoryRepository paymentCancelHistoryRepository;
    private final OrderRepository orderRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupMapper groupMapper;
    private final MakersRepository makersRepository;

    @Override
    @Transactional
    public GroupDto getGroupInfo(SecurityUser securityUser, Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));
        Corporation corporation = userUtil.getCorporation(securityUser);

        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findAllGroupOrderByFilter(corporation, startDate, endDate, null, null, null);

        return orderMapper.toGroupDtos(corporation, orderItemDailyFoods);
    }

    @Override
    @Transactional
    public OrderDto.GroupOrderItemDailyFoodList getOrder(SecurityUser securityUser, Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));
        List<BigInteger> spotIds = !parameters.containsKey("spots") || parameters.get("spots").equals("") ? null : StringUtils.parseBigIntegerList((String) parameters.get("spots"));
        Integer diningTypeCode = !parameters.containsKey("diningType") || parameters.get("diningType").equals("") ? null : Integer.parseInt((String) parameters.get("diningType"));
        BigInteger userId = !parameters.containsKey("userId") || parameters.get("userId").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("userId")));
        BigInteger makersId = !parameters.containsKey("makersId") || parameters.get("makersId").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("makersId")));

        Corporation corporation = userUtil.getCorporation(securityUser);
        Makers makers = (makersId != null) ? makersRepository.findById(makersId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS)) : null;

        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findAllByGroupFilter(startDate, endDate, corporation, spotIds, diningTypeCode, userId, makers);
        return orderMapper.toGroupOrderDto(orderItemDailyFoods);
    }

    @Override
    @Transactional
    public OrderDto.OrderDailyFoodDetail getOrderDetail(SecurityUser securityUser, String orderCode) {
        Corporation corporation = userUtil.getCorporation(securityUser);
        Order order = orderRepository.findOneByCode(orderCode).orElseThrow(() -> new ApiException(ExceptionEnum.ORDER_NOT_FOUND));

        // 주문한 식사가 해당 기업에 제공된 것인지 검증
        OrderItemDailyFood orderItemDailyFood = (OrderItemDailyFood) order.getOrderItems().get(0);
        if(!orderItemDailyFood.getDailyFood().getGroup().equals(corporation)) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }

        List<PaymentCancelHistory> paymentCancelHistories = paymentCancelHistoryRepository.findAllByOrderOrderByCancelDateTimeDesc(order);
        return orderMapper.orderToDetailDto((OrderDailyFood) order, paymentCancelHistories);

    }

    @Override
    @Transactional
    public List<OrderDto.OrderItemStatic> getOrderStatistic(SecurityUser securityUser, @RequestParam Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));

        Corporation corporation = userUtil.getCorporation(securityUser);
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findAllGroupOrderByFilter(corporation, startDate, endDate, null, null, null);
        List<UserGroup> userGroups = userGroupRepository.findAllByGroupAndClientStatus(corporation, ClientStatus.BELONG);

        return orderMapper.toOrderItemStatic(orderItemDailyFoods, userGroups);
    }
}
