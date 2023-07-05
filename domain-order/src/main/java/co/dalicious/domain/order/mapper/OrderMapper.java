package co.dalicious.domain.order.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.dto.ServiceDiningDto;
import co.dalicious.domain.order.dto.ExtraOrderDto;
import co.dalicious.domain.order.dto.GroupDto;
import co.dalicious.domain.order.entity.*;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.domain.order.dto.OrderDto;
import co.dalicious.system.util.NumberUtils;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {DateUtils.class, OrderDailyFood.class, Hibernate.class, OrderUtil.class, UserSupportPriceUtil.class, NumberUtils.class})
public interface OrderMapper {
    @Mapping(source = "orderCode", target = "code")
    @Mapping(source = "spot.address", target = "address")
    @Mapping(target = "paymentType", constant = "SUPPORT_PRICE")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "spot.group.name", target = "groupName")
    @Mapping(source = "spot.name", target = "spotName")
    @Mapping(source = "spot", target = "spot")
    @Mapping(target = "orderType", constant = "DAILYFOOD")
    OrderDailyFood toExtraOrderEntity(User user, Spot spot, String orderCode);

    @Mapping(target = "orderStatus", constant = "COMPLETED")
    @Mapping(target = "deliveryFee", constant = "0")
        // TODO: 배송비 추가가 필요한 경우 수정 필요
    OrderItemDailyFoodGroup toOrderItemDailyFoodGroup(ServiceDiningDto serviceDiningDto);

    @Mapping(source = "order", target = "order")
    @Mapping(target = "orderStatus", constant = "COMPLETED")
    @Mapping(source = "dailyFood", target = "dailyFood")
    @Mapping(source = "dailyFood.food.name", target = "name")
    @Mapping(source = "discountDto.price", target = "price")
    @Mapping(source = "extraOrderDto.count", target = "count")
    @Mapping(source = "extraOrderDto.usage", target = "usage")
    @Mapping(target = "discountedPrice", expression = "java(discountDto.getDiscountedPrice())")
    @Mapping(source = "discountDto.membershipDiscountRate", target = "membershipDiscountRate")
    @Mapping(source = "discountDto.makersDiscountRate", target = "makersDiscountRate")
    @Mapping(source = "discountDto.periodDiscountRate", target = "periodDiscountRate")
    @Mapping(source = "orderItemDailyFoodGroup", target = "orderItemDailyFoodGroup")
    OrderItemDailyFood toExtraOrderItemEntity(Order order, DailyFood dailyFood, ExtraOrderDto.Request extraOrderDto, DiscountDto discountDto, OrderItemDailyFoodGroup orderItemDailyFoodGroup);

    @Mapping(source = "orderCode", target = "code")
    @Mapping(source = "spot.address", target = "address")
    @Mapping(target = "paymentType", constant = "CREDIT_CARD") // TODO: 결제타입 넣기 로직 필요
    @Mapping(source = "user", target = "user")
    @Mapping(source = "spot.group.name", target = "groupName")
    @Mapping(source = "spot.name", target = "spotName")
    @Mapping(source = "spot", target = "spot")
    @Mapping(target = "orderType", constant = "DAILYFOOD")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "memo", target = "memo")
    OrderDailyFood toEntity(User user, Spot spot, String orderCode, String phone, String memo);

    @Mapping(source = "id", target = "orderItemDailyFoodId")
    @Mapping(source = "dailyFood.food.makers.name", target = "makers")
    @Mapping(source = "orderStatus.orderStatus", target = "orderStatus")
    @Mapping(source = "name", target = "foodName")
    @Mapping(target = "price", expression = "java(orderItemDailyFood.getOrderItemTotalPrice())")
    @Mapping(target = "supplyPrice", expression = "java(orderItemDailyFood.getOrderItemSupplyPrice())")
    @Mapping(source = "count", target = "count")
    @Mapping(target = "deliveryTime", expression = "java(DateUtils.timeToString(orderItemDailyFood.getDeliveryTime()))")
    OrderDto.OrderItemDailyFood orderItemDailyFoodToDto(OrderItemDailyFood orderItemDailyFood);

    @Mapping(source = "id", target = "orderItemDailyFoodId")
    @Mapping(source = "dailyFood.food.makers.name", target = "makers")
    @Mapping(source = "name", target = "foodName")
    @Mapping(target = "discountedPrice", expression = "java(orderItemDailyFood.getOrderItemTotalPrice())")
    @Mapping(source = "count", target = "count")
    @Mapping(source = "orderStatus.orderStatus", target = "orderStatus")
    OrderDto.OrderItemDailyFoodGroupItem orderItemDailyFoodGroupItemToDto(OrderItemDailyFood orderItemDailyFood);

    @Named("timeStampToString")
    default String timeStampToString(Timestamp timestamp) {
        return DateUtils.toISO(timestamp);
    }

    default List<OrderDto.OrderItemDailyFoodList> ToDtoByGroup(List<OrderItemDailyFood> orderItemDailyFoods) {
        MultiValueMap<Spot, OrderItemDailyFood> spotMap = new LinkedMultiValueMap<>();
        orderItemDailyFoods.forEach(orderItemDailyFood -> {
            OrderDailyFood orderDailyFood = (OrderDailyFood) Hibernate.unproxy(orderItemDailyFood.getOrder());
            spotMap.add(orderDailyFood.getSpot(), orderItemDailyFood);
        });

        return spotMap.entrySet().stream()
                .flatMap(spotEntry -> {
                    MultiValueMap<DiningType, OrderItemDailyFood> diningTypeMap = new LinkedMultiValueMap<>();
                    spotEntry.getValue().forEach(spotOrderItemDailyFood -> {
                        diningTypeMap.add(spotOrderItemDailyFood.getDailyFood().getDiningType(), spotOrderItemDailyFood);
                    });
                    return diningTypeMap.entrySet().stream()
                            .map(diningTypeEntry -> toOrderItemDailyFoodListDto(spotEntry.getKey(), diningTypeEntry.getKey(), diningTypeEntry.getValue()));
                })
                .collect(Collectors.toList());
    }

    ;

    default OrderDto.OrderItemDailyFoodList toOrderItemDailyFoodListDto(Spot spot, DiningType diningType, List<OrderItemDailyFood> orderItemDailyFoods) {
        OrderDto.OrderItemDailyFoodList orderItemDailyFoodList = new OrderDto.OrderItemDailyFoodList();
        orderItemDailyFoodList.setSpotId(spot.getId());
        orderItemDailyFoodList.setSpotFoodCount(getFoodCount(orderItemDailyFoods));
        orderItemDailyFoodList.setSpotName(spot.getName());
        orderItemDailyFoodList.setDiningType(diningType.getDiningType());
        orderItemDailyFoodList.setFoodMap(toFoodMap(orderItemDailyFoods));
        orderItemDailyFoodList.setOrderItemDailyFoodGroupList(toOrderItemDailyFoodGroupList(orderItemDailyFoods));
        return orderItemDailyFoodList;
    }

    default List<OrderDto.OrderItemDailyFoodGroupList> toOrderItemDailyFoodGroupList(List<OrderItemDailyFood> orderItemDailyFoods) {
        List<OrderDto.OrderItemDailyFoodGroupList> orderItemDailyFoodGroupList = new ArrayList<>();
        Set<OrderItemDailyFoodGroup> orderItemDailyFoodGroupSet = orderItemDailyFoods.stream()
                .map(OrderItemDailyFood::getOrderItemDailyFoodGroup)
                .collect(Collectors.toSet());
        for (OrderItemDailyFoodGroup orderItemDailyFoodGroup : orderItemDailyFoodGroupSet) {
            OrderDto.OrderItemDailyFoodGroupList orderItemDailyFoodGroupDto = new OrderDto.OrderItemDailyFoodGroupList();
            orderItemDailyFoodGroupDto.setServiceDate(DateUtils.format(orderItemDailyFoodGroup.getServiceDate()));
            orderItemDailyFoodGroupDto.setDiningType(orderItemDailyFoodGroup.getDiningType().getDiningType());
            orderItemDailyFoodGroupDto.setGroupName(((OrderDailyFood) Hibernate.unproxy(orderItemDailyFoodGroup.getOrderDailyFoods().get(0).getOrder())).getGroupName());
            orderItemDailyFoodGroupDto.setSpotName(((OrderDailyFood) Hibernate.unproxy(orderItemDailyFoodGroup.getOrderDailyFoods().get(0).getOrder())).getSpotName());
            orderItemDailyFoodGroupDto.setUserName(orderItemDailyFoodGroup.getOrderDailyFoods().get(0).getOrder().getUser().getName());
            orderItemDailyFoodGroupDto.setUserEmail(orderItemDailyFoodGroup.getOrderDailyFoods().get(0).getOrder().getUser().getEmail());
            orderItemDailyFoodGroupDto.setPhone(orderItemDailyFoodGroup.getOrderDailyFoods().get(0).getOrder().getUser().getPhone());
            orderItemDailyFoodGroupDto.setOrderCode(orderItemDailyFoodGroup.getOrderDailyFoods().get(0).getOrder().getCode());
            orderItemDailyFoodGroupDto.setOrderDateTime(timeStampToString(orderItemDailyFoodGroup.getOrderDailyFoods().get(0).getOrder().getCreatedDateTime()));
            orderItemDailyFoodGroupDto.setTotalPrice(orderItemDailyFoodGroup.getTotalPriceByGroup());
            orderItemDailyFoodGroupDto.setSupportPrice(orderItemDailyFoodGroup.getUsingSupportPrice());
            orderItemDailyFoodGroupDto.setPayPrice(orderItemDailyFoodGroup.getPayPrice());
            orderItemDailyFoodGroupDto.setDeliveryPrice((orderItemDailyFoodGroup.getOrderStatus() != OrderStatus.CANCELED) ? orderItemDailyFoodGroup.getDeliveryFee() : BigDecimal.ZERO);
            orderItemDailyFoodGroupDto.setOrderItemDailyFoods(orderItemDailyFoodsToDtos(orderItemDailyFoodGroup.getOrderDailyFoods()));

            orderItemDailyFoodGroupList.add(orderItemDailyFoodGroupDto);
        }
        return orderItemDailyFoodGroupList;
    }

    default List<OrderDto.OrderItemDailyFoodGroupList> toOrderItemDailyFoodGroupList(List<OrderItemDailyFood> orderItemDailyFoods, List<Membership> memberships) {
        List<OrderDto.OrderItemDailyFoodGroupList> orderItemDailyFoodGroupList = new ArrayList<>();
        Set<OrderItemDailyFoodGroup> orderItemDailyFoodGroupSet = orderItemDailyFoods.stream()
                .map(OrderItemDailyFood::getOrderItemDailyFoodGroup)
                .collect(Collectors.toSet());
        for (OrderItemDailyFoodGroup orderItemDailyFoodGroup : orderItemDailyFoodGroupSet) {
            Optional<Membership> membership = memberships.stream()
                    .filter(v -> v.getUser().equals(orderItemDailyFoodGroup.getOrderDailyFoods().get(0).getOrder().getUser()))
                    .findAny();

            OrderDto.OrderItemDailyFoodGroupList orderItemDailyFoodGroupDto = new OrderDto.OrderItemDailyFoodGroupList();
            orderItemDailyFoodGroupDto.setServiceDate(DateUtils.format(orderItemDailyFoodGroup.getServiceDate()));
            orderItemDailyFoodGroupDto.setDiningType(orderItemDailyFoodGroup.getDiningType().getDiningType());
            orderItemDailyFoodGroupDto.setGroupName(((OrderDailyFood) Hibernate.unproxy(orderItemDailyFoodGroup.getOrderDailyFoods().get(0).getOrder())).getGroupName());
            orderItemDailyFoodGroupDto.setSpotName(((OrderDailyFood) Hibernate.unproxy(orderItemDailyFoodGroup.getOrderDailyFoods().get(0).getOrder())).getSpotName());
            orderItemDailyFoodGroupDto.setUserName(orderItemDailyFoodGroup.getOrderDailyFoods().get(0).getOrder().getUser().getName());
            orderItemDailyFoodGroupDto.setUserEmail(orderItemDailyFoodGroup.getOrderDailyFoods().get(0).getOrder().getUser().getEmail());
            orderItemDailyFoodGroupDto.setPhone(orderItemDailyFoodGroup.getOrderDailyFoods().get(0).getOrder().getUser().getPhone());
            orderItemDailyFoodGroupDto.setOrderCode(orderItemDailyFoodGroup.getOrderDailyFoods().get(0).getOrder().getCode());
            orderItemDailyFoodGroupDto.setOrderDateTime(timeStampToString(orderItemDailyFoodGroup.getOrderDailyFoods().get(0).getOrder().getCreatedDateTime()));
            orderItemDailyFoodGroupDto.setTotalPrice(orderItemDailyFoodGroup.getTotalPriceByGroup());
            orderItemDailyFoodGroupDto.setSupportPrice(orderItemDailyFoodGroup.getUsingSupportPrice());
            orderItemDailyFoodGroupDto.setPayPrice(orderItemDailyFoodGroup.getPayPrice());
            orderItemDailyFoodGroupDto.setDeliveryPrice((orderItemDailyFoodGroup.getOrderStatus() != OrderStatus.CANCELED) ? orderItemDailyFoodGroup.getDeliveryFee() : BigDecimal.ZERO);
            orderItemDailyFoodGroupDto.setIsMembership(orderItemDailyFoodGroup.isMembershipApplied() || membership.isPresent());
            orderItemDailyFoodGroupDto.setOrderItemDailyFoods(orderItemDailyFoodsToDtos(orderItemDailyFoodGroup.getOrderDailyFoods()));

            orderItemDailyFoodGroupList.add(orderItemDailyFoodGroupDto);
        }
        return orderItemDailyFoodGroupList;
    }

    default Integer getFoodCount(List<OrderItemDailyFood> orderItemDailyFoods) {
        Integer count = 0;
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            count += orderItemDailyFood.getCount();
        }
        return count;
    }

    default List<OrderDto.OrderItemDailyFood> orderItemDailyFoodsToDtos(List<OrderItemDailyFood> orderItemDailyFoods) {
        return orderItemDailyFoods.stream()
                .map(this::orderItemDailyFoodToDto)
                .sorted(Comparator.comparing(OrderDto.OrderItemDailyFood::getOrderItemDailyFoodId))
                .collect(Collectors.toList());
    }

    default List<OrderDto.SpotFoodMap> toFoodMap(List<OrderItemDailyFood> orderItemDailyFoods) {
        Map<Food, Integer> foodMap = orderItemDailyFoods.stream()
                .collect(Collectors.groupingBy(v -> v.getDailyFood().getFood(), Collectors.summingInt(OrderItemDailyFood::getCount)));

        return foodMap.entrySet().stream()
                .map(entry -> {
                    OrderDto.SpotFoodMap spotFood = new OrderDto.SpotFoodMap();
                    spotFood.setFoodName(entry.getKey().getName());
                    spotFood.setCount(entry.getValue());
                    return spotFood;
                })
                .collect(Collectors.toList());
    }

    default OrderDto.OrderDailyFoodDetail orderToDetailDto(OrderDailyFood orderDailyFood, List<PaymentCancelHistory> paymentCancelHistories) {
        OrderDto.OrderDailyFoodDetail orderDailyFoodDetail = new OrderDto.OrderDailyFoodDetail();
        List<OrderDto.OrderItemDailyFoodGroup> orderItemDailyFoodGroups = toOrderItemDailyFoodGroupDto(orderDailyFood.getOrderItems());
        BigDecimal cancelPoint = BigDecimal.ZERO;
        BigDecimal cancelPrice = BigDecimal.ZERO;
        BigDecimal cancelDeliveryFee = BigDecimal.ZERO;
        for (PaymentCancelHistory paymentCancelHistory : paymentCancelHistories) {
            cancelPoint = cancelPoint.add(paymentCancelHistory.getRefundPointPrice());
            cancelPrice = cancelPrice.add(paymentCancelHistory.getCancelPrice());
            cancelDeliveryFee = cancelDeliveryFee.add(paymentCancelHistory.getRefundDeliveryFee());
        }

        LocalDate startDate = orderItemDailyFoodGroups.stream()
                .map(orderItemDailyFoodGroup -> DateUtils.stringToDate(orderItemDailyFoodGroup.getServiceDate()))
                .min(Comparator.naturalOrder())
                .orElse(LocalDate.now());

        LocalDate endDate = orderItemDailyFoodGroups.stream()
                .map(orderItemDailyFoodGroup -> DateUtils.stringToDate(orderItemDailyFoodGroup.getServiceDate()))
                .max(Comparator.naturalOrder())
                .orElse(LocalDate.now());

        BigDecimal totalSupportPrice = orderItemDailyFoodGroups.stream()
                .map(OrderDto.OrderItemDailyFoodGroup::getSupportPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        orderDailyFoodDetail.setOrderId(orderDailyFood.getId());
        orderDailyFoodDetail.setOrderCode(orderDailyFood.getCode());
        orderDailyFoodDetail.setUserName(orderDailyFood.getUser().getName());
        orderDailyFoodDetail.setServicePeriod(DateUtils.format(startDate) + " ~ " + DateUtils.format(endDate));
        orderDailyFoodDetail.setSpotName(orderDailyFood.getSpotName());
        orderDailyFoodDetail.setTotalPrice(orderDailyFood.getTotalPrice().subtract(cancelPrice));
        orderDailyFoodDetail.setUsingSupportPrice(totalSupportPrice);
        orderDailyFoodDetail.setDeliveryFee(orderDailyFood.getTotalDeliveryFee().subtract(cancelDeliveryFee));
        orderDailyFoodDetail.setPoint(orderDailyFood.getPoint().subtract(cancelPoint));
        orderDailyFoodDetail.setOrderItemDailyFoodGroups(orderItemDailyFoodGroups);

        return orderDailyFoodDetail;
    }

    default List<OrderDto.OrderItemDailyFoodGroup> toOrderItemDailyFoodGroupDto(List<OrderItem> orderItems) {
        Set<OrderItemDailyFoodGroup> orderItemDailyFoodGroupSet = new HashSet<>();
        List<OrderDto.OrderItemDailyFoodGroup> orderItemDailyFoodGroups = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            if ((OrderItem) Hibernate.unproxy(orderItem) instanceof OrderItemDailyFood orderItemDailyFood) {
                orderItemDailyFoodGroupSet.add(orderItemDailyFood.getOrderItemDailyFoodGroup());
            }
        }
        for (OrderItemDailyFoodGroup orderItemDailyFoodGroup : orderItemDailyFoodGroupSet) {
            OrderDto.OrderItemDailyFoodGroup orderItemDailyFoodGroupDto = new OrderDto.OrderItemDailyFoodGroup();
            orderItemDailyFoodGroupDto.setServiceDate(DateUtils.format(orderItemDailyFoodGroup.getServiceDate()));
            orderItemDailyFoodGroupDto.setDiningType(orderItemDailyFoodGroup.getDiningType().getDiningType());
            orderItemDailyFoodGroupDto.setTotalPrice(orderItemDailyFoodGroup.getTotalPriceByGroup());
            orderItemDailyFoodGroupDto.setSupportPrice(orderItemDailyFoodGroup.getUsingSupportPrice());
            orderItemDailyFoodGroupDto.setPayPrice(orderItemDailyFoodGroup.getPayPrice());
            orderItemDailyFoodGroupDto.setDeliveryPrice((orderItemDailyFoodGroup.getOrderStatus() != OrderStatus.CANCELED) ? orderItemDailyFoodGroup.getDeliveryFee() : BigDecimal.ZERO);
            orderItemDailyFoodGroupDto.setOrderItemDailyFoods(orderItemDailyFoodGroupItemsToDtos(orderItemDailyFoodGroup.getOrderDailyFoods()));

            orderItemDailyFoodGroups.add(orderItemDailyFoodGroupDto);
        }
        return orderItemDailyFoodGroups;
    }

    default List<OrderDto.OrderItemDailyFoodGroupItem> orderItemDailyFoodGroupItemsToDtos(List<OrderItemDailyFood> orderItemDailyFoods) {
        return orderItemDailyFoods.stream()
                .map(this::orderItemDailyFoodGroupItemToDto)
                .collect(Collectors.toList());
    }

    @Mapping(source = "id", target = "orderItemDailyFoodId")
    @Mapping(target = "serviceDate", expression = "java(DateUtils.format(orderItemDailyFood.getDailyFood().getServiceDate()))")
    @Mapping(source = "dailyFood.food.makers.name", target = "makers")
    @Mapping(source = "orderStatus.orderStatus", target = "orderStatus")
    @Mapping(source = "name", target = "foodName")
    @Mapping(target = "price", expression = "java(orderItemDailyFood.getOrderItemTotalPrice())")
    @Mapping(source = "count", target = "count")
    @Mapping(source = "order.user.name", target = "userName")
    @Mapping(source = "order.user.email", target = "userEmail")
    @Mapping(source = "order.user.phone", target = "phone")
    @Mapping(source = "order.code", target = "orderCode")
    @Mapping(source = "dailyFood.group.name", target = "groupName")
    @Mapping(target = "spotName", expression = "java(((OrderDailyFood) Hibernate.unproxy(orderItemDailyFood.getOrder())).getSpotName())")
    @Mapping(source = "dailyFood.diningType.diningType", target = "diningType")
    @Mapping(source = "createdDateTime", target = "orderDateTime", qualifiedByName = "timeStampToString")
    @Mapping(target = "deliveryTime", expression = "java(DateUtils.timeToString(orderItemDailyFood.getDeliveryTime()))")
    OrderDto.ClientOrderItemDailyFood orderItemDailyFoodToClientDto(OrderItemDailyFood orderItemDailyFood);

    default List<OrderDto.ClientOrderItemDailyFood> orderItemDailyFoodToClientDtos(List<OrderItemDailyFood> orderItemDailyFoods) {
        return orderItemDailyFoods.stream()
                .map(this::orderItemDailyFoodToClientDto)
                .toList();
    }


    default OrderDto.GroupOrderItemDailyFoodList toGroupOrderDto(List<OrderItemDailyFood> orderItemDailyFoods) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        Integer foodCount = 0;
        Set<User> buyingUser = new HashSet<>();
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            // 취소된 상품을 제외하고 계산
            if (OrderStatus.completePayment().contains(orderItemDailyFood.getOrderStatus())) {
                totalPrice = totalPrice.add(orderItemDailyFood.getOrderItemTotalPrice());
                foodCount += orderItemDailyFood.getCount();
                buyingUser.add(orderItemDailyFood.getOrder().getUser());
            }
        }
        List<OrderDto.ClientOrderItemDailyFood> orderItemDailyFoodDtos = orderItemDailyFoodToClientDtos(orderItemDailyFoods);

        OrderDto.GroupOrderItemDailyFoodList orderItemDailyFoodList = new OrderDto.GroupOrderItemDailyFoodList();
        orderItemDailyFoodList.setTotalPrice(totalPrice);
        orderItemDailyFoodList.setTotalFoodCount(foodCount);
        orderItemDailyFoodList.setBuyingUserCount(buyingUser.size());
        orderItemDailyFoodList.setOrderItemDailyFoods(orderItemDailyFoodDtos);

        return orderItemDailyFoodList;
    }

    default List<OrderDto.OrderItemStatic> toOrderItemStatic(List<OrderItemDailyFood> orderItemDailyFoods, List<UserGroup> userGroups) {
        List<OrderDto.OrderItemStatic> orderItemStatics = new ArrayList<>();
        MultiValueMap<ServiceDiningDto, OrderItemDailyFood> orderItemDailyFoodMultiValueMap = new LinkedMultiValueMap<>();
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            ServiceDiningDto serviceDiningDto = new ServiceDiningDto(orderItemDailyFood.getDailyFood().getServiceDate(), orderItemDailyFood.getDailyFood().getDiningType());
            orderItemDailyFoodMultiValueMap.add(serviceDiningDto, orderItemDailyFood);
        }
        for (ServiceDiningDto serviceDiningDto : orderItemDailyFoodMultiValueMap.keySet()) {
            List<OrderItemDailyFood> orderItemDailyFoodList = orderItemDailyFoodMultiValueMap.get(serviceDiningDto);

            Set<User> orderUsers = (orderItemDailyFoodList == null) ? Collections.emptySet() :
                    orderItemDailyFoodList.stream()
                            .map(v -> v.getOrder().getUser())
                            .collect(Collectors.toSet());
            Set<User> orderCompleteUsers = (orderItemDailyFoodList == null) ? Collections.emptySet() :
                    orderItemDailyFoodList.stream()
                            .filter(v -> OrderStatus.completePayment().contains(v.getOrderStatus()))
                            .map(v -> v.getOrder().getUser())
                            .collect(Collectors.toSet());

            Integer foodCount = 0;
            BigDecimal totalPrice = BigDecimal.ZERO;
            for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoodList) {
                if (OrderStatus.completePayment().contains(orderItemDailyFood.getOrderStatus())) {
                    foodCount += orderItemDailyFood.getCount();
                    totalPrice = totalPrice.add(orderItemDailyFood.getOrderItemTotalPrice());
                }
            }

            OrderDto.OrderItemStatic orderItemStatic = new OrderDto.OrderItemStatic();
            Integer userCount = UserGroup.activeUserCount(serviceDiningDto.getServiceDate(), userGroups).intValue();
            orderItemStatic.setServiceDate(DateUtils.format(serviceDiningDto.getServiceDate()));
            orderItemStatic.setDiningType(serviceDiningDto.getDiningType().getDiningType());
            orderItemStatic.setUserCount(userCount);
            orderItemStatic.setOrderUserCount(orderUsers.size());
            orderItemStatic.setFoodCount(foodCount);
            orderItemStatic.setBuyingUserCount(orderCompleteUsers.size());
            orderItemStatic.setOrderRate(NumberUtils.getPercent(userCount, orderCompleteUsers.size()));
            orderItemStatic.setCancelRate(NumberUtils.getPercent(orderUsers.size(), orderUsers.size() - orderCompleteUsers.size()));
            orderItemStatic.setTotalPrice(totalPrice);

            orderItemStatics.add(orderItemStatic);
        }

        orderItemStatics = orderItemStatics.stream()
                .sorted(Comparator.comparing((OrderDto.OrderItemStatic v) -> DateUtils.stringToDate(v.getServiceDate()))
                        .thenComparing(v -> DiningType.ofString(v.getDiningType()))
                )
                .collect(Collectors.toList());
        return orderItemStatics;
    }


    default GroupDto toGroupDtos(Group group, List<OrderItemDailyFood> orderItemDailyFoods) {
        GroupDto groupDto = new GroupDto();
        Set<User> users = new HashSet<>();
        Set<Makers> makers = new HashSet<>();
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            users.add(orderItemDailyFood.getOrder().getUser());
            makers.add(orderItemDailyFood.getDailyFood().getFood().getMakers());
        }
        groupDto.setSpots(spotsToDtos(group.getSpots()));
        groupDto.setUsers(userToDtos(users));
        groupDto.setDiningTypes(diningTypesToDtos(group.getDiningTypes()));
        groupDto.setMakers(makersToDtos(makers));
        return groupDto;
    }

    @Mapping(source = "id", target = "spotId")
    @Mapping(source = "name", target = "spotName")
    GroupDto.Spot spotToDto(Spot spot);

    @Mapping(source = "diningType", target = "diningType")
    @Mapping(source = "code", target = "code")
    GroupDto.DiningType diningTypeToDto(DiningType diningType);

    @Mapping(source = "id", target = "userId")
    @Mapping(source = "name", target = "userName")
    GroupDto.User userToDto(User user);

    @Mapping(source = "id", target = "makersId")
    @Mapping(source = "name", target = "makersName")
    GroupDto.Makers makersToDto(Makers makers);

    default List<GroupDto.User> userToDtos(Set<User> users) {
        return users.stream()
                .map(this::userToDto)
                .collect(Collectors.toList());
    }

    default List<GroupDto.Spot> spotsToDtos(List<Spot> spots) {
        return spots.stream()
                .map(this::spotToDto)
                .collect(Collectors.toList());
    }

    default List<GroupDto.Makers> makersToDtos(Set<Makers> makers) {
        return makers.stream()
                .map(this::makersToDto)
                .collect(Collectors.toList());
    }

    default List<GroupDto.DiningType> diningTypesToDtos(List<DiningType> diningTypes) {
        return diningTypes.stream()
                .map(this::diningTypeToDto)
                .collect(Collectors.toList());
    }
}

