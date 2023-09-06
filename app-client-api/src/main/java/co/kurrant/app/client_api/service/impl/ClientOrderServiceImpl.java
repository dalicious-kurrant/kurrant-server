package co.kurrant.app.client_api.service.impl;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.delivery.utils.DeliveryUtils;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.domain.food.repository.MakersRepository;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.order.dto.*;
import co.dalicious.domain.order.entity.*;
import co.dalicious.domain.order.entity.enums.MonetaryStatus;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.order.mapper.DailyFoodSupportPriceMapper;
import co.dalicious.domain.order.mapper.ExtraOrderMapper;
import co.dalicious.domain.order.mapper.OrderMapper;
import co.dalicious.domain.order.repository.*;
import co.dalicious.domain.order.util.OrderDailyFoodUtil;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.user.converter.RefundPriceDto;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.repository.QUserRepository;
import co.dalicious.domain.user.repository.UserGroupRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.PeriodDto;
import co.dalicious.system.util.StringUtils;
import co.kurrant.app.client_api.model.SecurityUser;
import co.kurrant.app.client_api.service.ClientOrderService;
import co.kurrant.app.client_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ClientOrderServiceImpl implements ClientOrderService {
    private final QOrderItemDailyFoodRepository qOrderItemDailyFoodRepository;
    private final UserUtil userUtil;
    private final OrderMapper orderMapper;
    private final PaymentCancelHistoryRepository paymentCancelHistoryRepository;
    private final OrderRepository orderRepository;
    private final UserGroupRepository userGroupRepository;
    private final MakersRepository makersRepository;
    private final QDailyFoodRepository qDailyFoodRepository;
    private final ExtraOrderMapper extraOrderMapper;
    private final QUserRepository qUserRepository;
    private final OrderItemDailyFoodGroupRepository orderItemDailyFoodGroupRepository;
    private final OrderItemDailyFoodRepository orderItemDailyFoodRepository;
    private final DailyFoodSupportPriceMapper dailyFoodSupportPriceMapper;
    private final OrderDailyFoodUtil orderDailyFoodUtil;
    private final DailyFoodSupportPriceRepository dailyFoodSupportPriceRepository;
    private final OrderDailyFoodRepository orderDailyFoodRepository;
    private final DeliveryUtils deliveryUtils;

    @Override
    @Transactional
    public GroupDto getGroupInfo(SecurityUser securityUser, Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));
        Corporation corporation = userUtil.getCorporation(securityUser);

        List<OrderItemDailyFood> orderItemDailyFoods = qOrderItemDailyFoodRepository.findAllGroupOrderByFilter(corporation, startDate, endDate, null, null, null);

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

        List<SelectOrderDailyFoodDto> selectOrderDailyFoodDtos = qOrderItemDailyFoodRepository.findSelectDtoByGroupFilter(startDate, endDate, GroupDataType.CORPORATION.getCode(), corporation, spotIds, diningTypeCode, userId, makers, null);
        return orderMapper.toGroupOrderDtoList(selectOrderDailyFoodDtos);
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
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderItemDailyFoodRepository.findAllGroupOrderByFilter(corporation, startDate, endDate, null, null, null);
        List<UserGroup> userGroups = userGroupRepository.findAllByGroup(corporation);

        return orderMapper.toOrderItemStatic(orderItemDailyFoods, userGroups);
    }

    @Override
    @Transactional
    public List<ExtraOrderDto.DailyFoodList> getExtraDailyFoods(SecurityUser securityUser, LocalDate startDate, LocalDate endDate) {
        Corporation corporation = userUtil.getCorporation(securityUser);
        List<DailyFood> dailyFoods = qDailyFoodRepository.findAllByGroupAndMakersBetweenServiceDate(startDate, endDate, Collections.singletonList(corporation.getId()), null);
        Map<DailyFood, Integer> remainFoodCount = orderDailyFoodUtil.getRemainFoodsCount(dailyFoods);
        return extraOrderMapper.toDailyFoodList(dailyFoods, remainFoodCount);
    }

    @Override
    @Transactional
    public void postExtraOrderItems(SecurityUser securityUser, List<ExtraOrderDto.Request> orderDtos) {
        Corporation corporation = userUtil.getCorporation(securityUser);
        List<Spot> spots = corporation.getSpots();
        List<User> users = qUserRepository.findManagerByGroupIds(Collections.singleton(corporation.getId()));

        // 그룹에 속한 매니저가 없다면 예외 처리
        if(users.size() != 1) throw new ApiException(ExceptionEnum.USER_NOT_FOUND);

        // 추가하고자 하는 식단 리스트 가져오기
        Set<ServiceDiningVo> serviceDiningVos = new HashSet<>();
        MultiValueMap<BigInteger, ExtraOrderDto.Request> requestMap = new LinkedMultiValueMap<>();
        for (ExtraOrderDto.Request orderDto : orderDtos) {
            requestMap.add(orderDto.getSpotId(), orderDto);

            LocalDate serviceDate = DateUtils.stringToDate(orderDto.getServiceDate());
            DiningType diningType = DiningType.ofString(orderDto.getDiningType());
            ServiceDiningVo serviceDiningVo = new ServiceDiningVo(serviceDate, diningType);
            serviceDiningVos.add(serviceDiningVo);
        }
        PeriodDto periodDto = UserSupportPriceUtil.getEarliestAndLatestServiceDate(serviceDiningVos);
        List<DailyFood> dailyFoods = qDailyFoodRepository.findAllByGroupAndMakersBetweenServiceDate(periodDto.getStartDate(), periodDto.getEndDate(), Collections.singletonList(corporation.getId()), null);

        dailyFoods.stream().filter(v -> v.getDailyFoodStatus().equals(DailyFoodStatus.SALES))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.SOLD_OUT));

        // 식단을 스팟별로 정렬
        for (BigInteger bigInteger : requestMap.keySet()) {
            Spot spot = spots.stream()
                    .filter(v -> v.getId().equals(bigInteger))
                    .findAny()
                    .orElseThrow(() -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND));

            List<ExtraOrderDto.Request> requestsBySpot = requestMap.get(bigInteger);

            // 주문서 저장
            String code = OrderUtil.generateOrderCode(OrderType.DAILYFOOD, users.get(0).getId());
            OrderDailyFood order = orderDailyFoodRepository.save(orderMapper.toExtraOrderEntity(users.get(0), spot, code));

            BigDecimal defaultPrice = BigDecimal.ZERO;

            assert requestsBySpot != null;

            // 식사일정별로 DailyFood 묶기 (OrderItemDailyFoodGroup)
            MultiValueMap<ServiceDiningVo, ExtraOrderDto.Request> orderDailyFoodGroupMap = new LinkedMultiValueMap<>();
            for (ExtraOrderDto.Request request : requestsBySpot) {
                ServiceDiningVo serviceDiningVo = new ServiceDiningVo(DateUtils.stringToDate(request.getServiceDate()), DiningType.ofString(request.getDiningType()));
                orderDailyFoodGroupMap.add(serviceDiningVo, request);
            }

            for (ServiceDiningVo serviceDiningVo : orderDailyFoodGroupMap.keySet()) {
                OrderItemDailyFoodGroup orderItemDailyFoodGroup = orderItemDailyFoodGroupRepository.save(orderMapper.toOrderItemDailyFoodGroup(serviceDiningVo));

                List<ExtraOrderDto.Request> requests = orderDailyFoodGroupMap.get(serviceDiningVo);
                List<OrderItemDailyFood> orderItemDailyFoods = new ArrayList<>();
                assert requests != null;
                BigDecimal supportPrice = BigDecimal.ZERO;
                for (ExtraOrderDto.Request request : requests) {
                    DailyFood dailyFood = dailyFoods.stream()
                            .filter(v -> v.getServiceDate().equals(DateUtils.stringToDate(request.getServiceDate())) &&
                                    v.getDiningType().equals(DiningType.ofString(request.getDiningType())) &&
                                    v.getFood().getId().equals(request.getFoodId()))
                            .findAny()
                            .orElse(null);

                    assert dailyFood != null;
                    DiscountDto discountDto = DiscountDto.getDiscountWithoutMembership(dailyFood.getFood());

                    LocalTime tempDeliveryTime = spot.getMealInfo(dailyFood.getDiningType()).getDeliveryTimes().get(0);
                    OrderItemDailyFood orderItemDailyFood = orderItemDailyFoodRepository.save(orderMapper.toExtraOrderItemEntity(order, dailyFood, request, discountDto, orderItemDailyFoodGroup, tempDeliveryTime));
                    orderItemDailyFoods.add(orderItemDailyFood);
                    // TODO: 배송시간 추가
                    deliveryUtils.saveDeliveryInstance(orderItemDailyFood, spot, null, dailyFood, tempDeliveryTime);
                    defaultPrice = defaultPrice.add(dailyFood.getFood().getPrice().multiply(BigDecimal.valueOf(request.getCount())));
                    supportPrice = supportPrice.add(orderItemDailyFood.getOrderItemTotalPrice());
                }
                DailyFoodSupportPrice dailyFoodSupportPrice = dailyFoodSupportPriceMapper.toEntity(orderItemDailyFoods.get(0), supportPrice);
                dailyFoodSupportPriceRepository.save(dailyFoodSupportPrice);
            }

            order.updateDefaultPrice(defaultPrice);
            order.updateTotalPrice(BigDecimal.ZERO);
            order.updateTotalDeliveryFee(BigDecimal.ZERO);
            order.updatePoint(BigDecimal.ZERO);
        }
    }

    @Override
    @Transactional
    public List<ExtraOrderDto.Response> getExtraOrders(SecurityUser securityUser, Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));

        Corporation corporation = userUtil.getCorporation(securityUser);
        List<User> users = qUserRepository.findAdminAndManagerByGroupIds(Collections.singleton(corporation.getId()));
        List<BigInteger> userIds = users.stream()
                .map(User::getId)
                .toList();
        List<OrderItemDailyFood> orderDailyFoods =  qOrderItemDailyFoodRepository.findExtraOrdersByManagerId(userIds, startDate, endDate, null);

        return extraOrderMapper.toExtraOrderDtos(orderDailyFoods);
    }

    @Override
    @Transactional
    public void refundExtraOrderItems(SecurityUser securityUser, OrderDto.Id id) {
        List<User> users = qUserRepository.findAdminAndManagerByGroupIds(Collections.singleton(securityUser.getId()));

        OrderItemDailyFood orderItemDailyFood = orderItemDailyFoodRepository.findById(id.getId())
                .orElseThrow(() -> new ApiException(ExceptionEnum.ORDER_NOT_FOUND));

        if (!users.contains(orderItemDailyFood.getOrder().getUser())) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }

        if (!orderItemDailyFood.getOrderStatus().equals(OrderStatus.COMPLETED) || orderItemDailyFood.getOrderItemDailyFoodGroup().getOrderStatus().equals(OrderStatus.CANCELED)) {
            throw new ApiException(ExceptionEnum.DUPLICATE_CANCELLATION_REQUEST);
        }

        if (orderItemDailyFood.getDailyFood().getDailyFoodStatus().equals(DailyFoodStatus.STOP_SALE) || orderItemDailyFood.getDailyFood().getDailyFoodStatus().equals(DailyFoodStatus.PASS_LAST_ORDER_TIME)) {
            throw new ApiException(ExceptionEnum.LAST_ORDER_TIME_PASSED);
        }

        RefundPriceDto refundPriceDto = OrderUtil.getRefundPrice(orderItemDailyFood, null, null);

        BigDecimal usedSupportPrice = orderItemDailyFood.getOrderItemDailyFoodGroup().getUsingSupportPrice();

        if (!refundPriceDto.isSameSupportPrice(usedSupportPrice)) {
            List<DailyFoodSupportPrice> userSupportPriceHistories = orderItemDailyFood.getOrderItemDailyFoodGroup().getUserSupportPriceHistories();
            for (DailyFoodSupportPrice dailyFoodSupportPrice : userSupportPriceHistories) {
                dailyFoodSupportPrice.updateMonetaryStatus(MonetaryStatus.REFUND);
            }
            DailyFoodSupportPrice dailyFoodSupportPrice = dailyFoodSupportPriceMapper.toEntity(orderItemDailyFood, refundPriceDto.getRenewSupportPrice());
            if (dailyFoodSupportPrice.getUsingSupportPrice().compareTo(BigDecimal.ZERO) != 0) {
                dailyFoodSupportPriceRepository.save(dailyFoodSupportPrice);
            }
        }

        orderItemDailyFood.updateOrderStatus(OrderStatus.CANCELED);
        if (refundPriceDto.getIsLastItemOfGroup()) {
            orderItemDailyFood.getOrderItemDailyFoodGroup().updateOrderStatus(OrderStatus.CANCELED);
        }
    }
}
