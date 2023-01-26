package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.client.entity.CorporationSpot;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.SpotRepository;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.util.FoodUtil;
import co.dalicious.domain.order.dto.CartDailyFoodDto;
import co.dalicious.domain.order.dto.DiningTypeServiceDate;
import co.dalicious.domain.order.dto.OrderItemDailyFoodReqDto;
import co.dalicious.domain.order.entity.CartDailyFood;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.UserSupportPriceHistory;
import co.dalicious.domain.order.mapper.OrderDailyFoodItemMapper;
import co.dalicious.domain.order.mapper.OrderDailyFoodMapper;
import co.dalicious.domain.order.mapper.UserSupportPriceHistoryReqMapper;
import co.dalicious.domain.order.repository.*;
import co.dalicious.domain.order.service.DeliveryFeePolicy;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.PeriodDto;
import co.dalicious.system.util.enums.DiningType;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.OrderDailyFoodService;
import co.kurrant.app.public_api.service.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDailyFoodServiceImpl implements OrderDailyFoodService {
    private final UserUtil userUtil;
    private final SpotRepository spotRepository;
    private final QCartDailyFoodRepository qCartDailyFoodRepository;
    private final UserSupportPriceUtil userSupportPriceUtil;
    private final QUserSupportPriceHistoryRepository qUserSupportPriceHistoryRepository;
    private final DeliveryFeePolicy deliveryFeePolicy;
    private final OrderDailyFoodMapper orderDailyFoodMapper;
    private final OrderDailyFoodItemMapper orderDailyFoodItemMapper;
    private final OrderDailyFoodRepository orderDailyFoodRepository;
    private final OrderItemDailyFoodRepository orderItemDailyFoodRepository;
    private final UserSupportPriceHistoryReqMapper userSupportPriceHistoryReqMapper;
    private final UserSupportPriceHistoryRepository userSupportPriceHistoryRepository;

    @Override
    @Transactional
    public void orderDailyFoods(SecurityUser securityUser, OrderItemDailyFoodReqDto orderItemDailyFoodReqDto, BigInteger spotId) {
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

        // 그룹/스팟 정보 가져오기
        Spot spot = spotRepository.findById(spotId).orElseThrow(
                () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
        );
        Group group = spot.getGroup();
        // 유저가 그 그룹의 스팟에 포함되는지 확인.
        List<UserGroup> userGroups = user.getGroups();
        userGroups.stream().filter(v -> v.getGroup().equals(group) && v.getClientStatus().equals(ClientStatus.BELONG))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.UNAUTHORIZED));

        // 식사타입(DiningType)과 날짜별(serviceDate) 식사들 가져오기
        List<CartDailyFoodDto> cartDailyFoodDtoList = orderItemDailyFoodReqDto.getCartDailyFoodDtoList();
        List<DiningTypeServiceDate> diningTypeServiceDates = new ArrayList<>();
        List<BigInteger> cartDailyFoodIds = new ArrayList<>();
        BigDecimal totalDeliveryFee = BigDecimal.ZERO;
        BigDecimal defaultPrice = BigDecimal.ZERO;
        BigDecimal totalDailyFoodPrice = BigDecimal.ZERO;

        // 프론트에서 제공한 정보와 실제 정보가 일치하는지 확인
        for (CartDailyFoodDto cartDailyFoodDto : cartDailyFoodDtoList) {
            // 배송비 일치 점증 및 배송비 계산
            if (cartDailyFoodDto.getDeliveryFee().compareTo(deliveryFeePolicy.getGroupDeliveryFee(user, group)) != 0) {
                throw new ApiException(ExceptionEnum.NOT_MATCHED_DELIVERY_FEE);
            }
            totalDeliveryFee = totalDeliveryFee.add(cartDailyFoodDto.getDeliveryFee());

            diningTypeServiceDates.add(new DiningTypeServiceDate(DateUtils.stringToDate(cartDailyFoodDto.getServiceDate()), DiningType.ofCode(Integer.parseInt(cartDailyFoodDto.getDiningType()))));
            for (CartDailyFoodDto.DailyFood dailyFood : cartDailyFoodDto.getCartDailyFoods()) {
                cartDailyFoodIds.add(dailyFood.getId());
            }
        }

        // ServiceDate의 가장 빠른 날짜와 늦은 날짜 구하기
        PeriodDto periodDto = userSupportPriceUtil.getEarliestAndLatestServiceDate(diningTypeServiceDates);

        List<CartDailyFood> cartDailyFoods = qCartDailyFoodRepository.findAllByFoodIds(cartDailyFoodIds);

        // ServiceDate의 가장 빠른 날짜와 늦은 날짜 사이에 유저가 사용한 회사 지원금 가져오기
        List<UserSupportPriceHistory> userSupportPriceHistories = qUserSupportPriceHistoryRepository.findAllUserSupportPriceHistoryBySpotBetweenServiceDate(user, group, periodDto.getStartDate(), periodDto.getEndDate());

        // 1. 주문서 저장하기
        OrderDailyFood orderDailyFood = orderDailyFoodRepository.save(orderDailyFoodMapper.toEntity(user, spot));

        for (CartDailyFoodDto cartDailyFoodDto : cartDailyFoodDtoList) {
            // 2. 유저 사용가능 지원금 일치 검증
            BigDecimal supportPrice = BigDecimal.ZERO;
            if (spot instanceof CorporationSpot) {
                supportPrice = userSupportPriceUtil.getGroupSupportPriceByDiningType(spot, DiningType.ofCode(Integer.parseInt(cartDailyFoodDto.getDiningType())));
                // 기존에 사용한 지원금이 있다면 차감
                BigDecimal usedSupportPrice = userSupportPriceUtil.getUsedSupportPrice(userSupportPriceHistories, DateUtils.stringToDate(cartDailyFoodDto.getServiceDate()));
                supportPrice = supportPrice.subtract(usedSupportPrice);
                if (cartDailyFoodDto.getSupportPrice().compareTo(supportPrice) != 0) {
                    throw new ApiException(ExceptionEnum.NOT_MATCHED_SUPPORT_PRICE);
                }
                // 3. 주문 음식 가격이 일치하는지 검증 및 주문 저장
                for (CartDailyFoodDto.DailyFood cartDailyFood : cartDailyFoodDto.getCartDailyFoods()) {
                    CartDailyFood selectedCartDailyFood = cartDailyFoods.stream().filter(v -> v.getId().equals(cartDailyFood.getId()))
                            .findAny()
                            .orElseThrow(() -> new ApiException(ExceptionEnum.DAILY_FOOD_NOT_FOUND));
                    // 주문 수량이 일치하는지 확인
                    if (!selectedCartDailyFood.getCount().equals(cartDailyFood.getCount())) {
                        throw new ApiException(ExceptionEnum.NOT_MATCHED_ITEM_COUNT);
                    }
                    // 멤버십에 가입하지 않은 경우 멤버십 할인이 적용되지 않은 가격으로 보임
                    DiscountDto discountDto = DiscountDto.getDiscount(selectedCartDailyFood.getDailyFood().getFood());
                    OrderUtil.checkMembershipAndUpdateDiscountDto(user, group, discountDto);
                    // 금액 일치 확인
                    if (cartDailyFood.getDiscountedPrice().compareTo(FoodUtil.getFoodTotalDiscountedPrice(selectedCartDailyFood.getDailyFood().getFood(), discountDto)) != 0) {
                        throw new ApiException(ExceptionEnum.NOT_MATCHED_PRICE);
                    }
                    if (cartDailyFood.getPrice().compareTo(selectedCartDailyFood.getDailyFood().getFood().getPrice()) != 0 ||
                            cartDailyFood.getMakersDiscountRate().compareTo(discountDto.getMakersDiscountRate()) != 0 ||
                            cartDailyFood.getMakersDiscountPrice().compareTo(discountDto.getMakersDiscountPrice()) != 0 ||
                            cartDailyFood.getMembershipDiscountRate().compareTo(discountDto.getMembershipDiscountRate()) != 0 ||
                            cartDailyFood.getMembershipDiscountPrice().compareTo(discountDto.getMembershipDiscountPrice()) != 0 ||
                            cartDailyFood.getPeriodDiscountRate().compareTo(discountDto.getPeriodDiscountRate()) != 0 ||
                            cartDailyFood.getPeriodDiscountPrice().compareTo(discountDto.getPeriodDiscountPrice()) != 0
                    ) {
                        throw new ApiException(ExceptionEnum.NOT_MATCHED_PRICE);
                    }
                    OrderItemDailyFood orderItemDailyFood = orderDailyFoodItemMapper.toEntity(cartDailyFood, selectedCartDailyFood, orderDailyFood);
                    orderItemDailyFoodRepository.save(orderItemDailyFood);

                    defaultPrice = defaultPrice.add(selectedCartDailyFood.getDailyFood().getFood().getPrice());
                    totalDailyFoodPrice = totalDailyFoodPrice.add(cartDailyFood.getPrice().subtract(cartDailyFood.getDiscountedPrice()).multiply(BigDecimal.valueOf(cartDailyFood.getCount())));

                    // 지원금 사용 저장
                    BigDecimal usableSupportPrice = UserSupportPriceUtil.getUsableSupportPrice(totalDailyFoodPrice, supportPrice);
                    UserSupportPriceHistory userSupportPriceHistory = userSupportPriceHistoryReqMapper.toEntity(orderItemDailyFood, usableSupportPrice);
                    userSupportPriceHistoryRepository.save(userSupportPriceHistory);
                }
            }
            qCartDailyFoodRepository.deleteByCartDailyFoodList(cartDailyFoods);
        }
    }

    @Override
    @Transactional
    public Object findOrderByServiceDate(LocalDate startDate, LocalDate endDate) {
//        List<OrderDetailDto> resultList = new ArrayList<>();
//        OrderDetailDto orderDetailDto = new OrderDetailDto();
//
//        List<OrderItemDto> orderItemDtoList = new ArrayList<>();
//
//        List<OrderDailyFood> orderItemList = qOrderDailyFoodRepository.findByServiceDateBetween(startDate, endDate);
//
//        orderItemList.forEach(x -> {
//            orderDetailDto.setId(x.getId());
//            orderDetailDto.setServiceDate(DateUtils.format(x.getServiceDate(), "yyyy-MM-dd"));
//
//            Optional<Food> food = foodRepository.findOneById(x.getId());
//
//            OrderItemDto orderItemDto = orderDetailMapper.toOrderItemDto(food.get(), x);
//
//            orderItemDtoList.add(orderItemDto);
//            orderDetailDto.setOrderItemDtoList(orderItemDtoList);
//            resultList.add(orderDetailDto);
//        });
//        return resultList;
        return null;
    }
}
