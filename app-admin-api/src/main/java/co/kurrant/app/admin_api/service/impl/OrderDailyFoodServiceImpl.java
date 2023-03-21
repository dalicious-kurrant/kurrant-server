package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.repository.ApartmentRepository;
import co.dalicious.domain.client.repository.CorporationRepository;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.MakersRepository;
import co.dalicious.domain.order.dto.OrderDailyFoodByMakersDto;
import co.dalicious.domain.order.entity.*;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.mapper.OrderDailyFoodByMakersMapper;
import co.dalicious.domain.order.repository.OrderItemRepository;
import co.dalicious.domain.order.repository.OrderRepository;
import co.dalicious.domain.order.repository.PaymentCancelHistoryRepository;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.domain.order.service.OrderService;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.entity.enums.ClientType;
import co.dalicious.domain.user.entity.enums.UserStatus;
import co.dalicious.domain.user.repository.UserGroupRepository;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.StringUtils;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.MakersDto;
import co.dalicious.domain.order.dto.OrderDto;
import co.kurrant.app.admin_api.mapper.GroupMapper;
import co.kurrant.app.admin_api.mapper.MakersMapper;
import co.dalicious.domain.order.mapper.OrderMapper;
import co.kurrant.app.admin_api.service.OrderDailyFoodService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderDailyFoodServiceImpl implements OrderDailyFoodService {
    private final GroupRepository groupRepository;
    private final ApartmentRepository apartmentRepository;
    private final CorporationRepository corporationRepository;
    private final GroupMapper groupMapper;
    private final OrderMapper orderMapper;
    private final MakersMapper makersMapper;
    private final UserGroupRepository userGroupRepository;
    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    private final MakersRepository makersRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final OrderItemRepository orderItemRepository;
    private final OrderDailyFoodByMakersMapper orderDailyFoodByMakersMapper;
    private final PaymentCancelHistoryRepository paymentCancelHistoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public List<OrderDto.OrderItemDailyFoodList> retrieveOrder(Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));
        BigInteger groupId = !parameters.containsKey("group") || parameters.get("group").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("group")));
        List<BigInteger> spotIds = !parameters.containsKey("spots") || parameters.get("spots").equals("") ? null : StringUtils.parseBigIntegerList((String) parameters.get("spots"));
        Integer diningTypeCode = !parameters.containsKey("diningType") || parameters.get("diningType").equals("") ? null : Integer.parseInt((String) parameters.get("diningType"));
        BigInteger userId = !parameters.containsKey("userId") || parameters.get("userId").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("userId")));
        BigInteger makersId = !parameters.containsKey("makersId") || parameters.get("makersId").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("makersId")));

        Group group = (groupId != null) ? groupRepository.findById(groupId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND)) : null;
        Makers makers = (makersId != null) ? makersRepository.findById(makersId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS)) : null;

        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findAllByGroupFilter(startDate, endDate, group, spotIds, diningTypeCode, userId, makers);

        return orderMapper.ToDtoByGroup(orderItemDailyFoods);
    }

    @Override
    @Transactional
    public OrderDailyFoodByMakersDto.ByPeriod retrieveOrderByMakers(Map<String, Object> parameters) {
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("startDate"));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate").equals("") ? null : DateUtils.stringToDate((String) parameters.get("endDate"));
        List<Integer> diningTypes = !parameters.containsKey("diningTypes") || parameters.get("diningTypes").equals("") ? null : StringUtils.parseIntegerList((String) parameters.get("diningTypes"));
        BigInteger makersId = !parameters.containsKey("makersId") || parameters.get("makersId").equals("") ? null : BigInteger.valueOf(Integer.parseInt((String) parameters.get("makersId")));

        assert makersId != null;
        Makers makers = makersRepository.findById(makersId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS));

        List<OrderItemDailyFood> orderItemDailyFoodList = qOrderDailyFoodRepository.findAllByMakersFilter(startDate, endDate, makers, diningTypes);

        return orderDailyFoodByMakersMapper.toDto(orderItemDailyFoodList);
    }

    @Override
    @Transactional
    public OrderDto.OrderDailyFoodDetail getOrderDetail(String orderCode) {
        Order order = orderRepository.findOneByCode(orderCode).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        List<PaymentCancelHistory> paymentCancelHistories = paymentCancelHistoryRepository.findAllByOrderOrderByCancelDateTimeDesc(order);
        return orderMapper.orderToDetailDto((OrderDailyFood) order, paymentCancelHistories);
    }

    @Override
    public List<GroupDto.Group> getGroup(Integer clientType) {
        List<? extends Group> groups = new ArrayList<>();
        if(clientType == null) {
            groups = groupRepository.findAll();
        } else if (ClientType.ofCode(clientType) == ClientType.APARTMENT) {
            groups = apartmentRepository.findAll();
        } else if (ClientType.ofCode(clientType) == ClientType.CORPORATION) {
            groups = corporationRepository.findAll();
        }

        return groupMapper.groupsToDtos(groups);
    }

    @Override
    @Transactional
    public GroupDto getGroupInfo(BigInteger groupId) {
        if(groupId == null) {
            List<User> users = userRepository.findAllByUserStatus(UserStatus.ACTIVE);
            return groupMapper.groupToGroupDto(null, users);
        }

        List<User> users = new ArrayList<>();
        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND)
        );
        List<UserGroup> userGroups = userGroupRepository.findAllByGroupAndClientStatus(group, ClientStatus.BELONG);
        for (UserGroup userGroup : userGroups) {
            users.add(userGroup.getUser());
        }
        return groupMapper.groupToGroupDto(group, users);
    }

    @Override
    public List<MakersDto.Makers> getMakers() {
        return makersMapper.makersToDtos(makersRepository.findAll());
    }

    @Override
    @Transactional
    public void cancelOrder(BigInteger orderId) throws IOException, ParseException {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ApiException(ExceptionEnum.ORDER_NOT_FOUND));
        User user = order.getUser();

        if(order instanceof OrderDailyFood orderDailyFood) {
            orderService.cancelOrderDailyFood(orderDailyFood, user);
        }
    }

    @Override
    @Transactional
    public void changeOrderStatus(OrderDto.StatusAndIdList statusAndIdList) {
        OrderStatus orderStatus = OrderStatus.ofCode(statusAndIdList.getStatus());
        if(!OrderStatus.completePayment().contains(orderStatus)) {
            throw new ApiException(ExceptionEnum.CANNOT_CHANGE_STATUS);
        }
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findAllByIds(statusAndIdList.getIdList());
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            if (!OrderStatus.completePayment().contains(orderItemDailyFood.getOrderStatus())) {
                throw new ApiException(ExceptionEnum.CANNOT_CHANGE_STATUS);
            }
            orderItemDailyFood.updateOrderStatus(orderStatus);
        }
    }

    @Override
    @Transactional
    public void cancelOrderItems(List<BigInteger> orderItemList) throws IOException, ParseException {
        List<OrderItem> orderItems = orderItemRepository.findAllByIds(orderItemList);

        for (OrderItem orderItem : orderItems) {
            User user = (User) Hibernate.unproxy(orderItem.getOrder().getUser());

            if(orderItem instanceof OrderItemDailyFood orderItemDailyFood) {
                orderService.cancelOrderItemDailyFood(orderItemDailyFood, user);
            }
        }
    }
}
