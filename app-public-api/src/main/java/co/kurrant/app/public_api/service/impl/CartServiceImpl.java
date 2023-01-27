package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.repository.DailyFoodRepository;
import co.dalicious.domain.order.dto.*;
import co.dalicious.domain.order.entity.Cart;
import co.dalicious.domain.order.entity.CartDailyFood;
import co.dalicious.domain.order.entity.UserSupportPriceHistory;
import co.dalicious.domain.order.mapper.CartDailyFoodMapper;
import co.dalicious.domain.order.mapper.CartDailyFoodsResMapper;
import co.dalicious.domain.order.repository.*;
import co.dalicious.domain.order.service.DeliveryFeePolicy;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.PeriodDto;
import co.dalicious.system.util.enums.DiningType;
import co.dalicious.system.util.enums.FoodStatus;
import co.kurrant.app.public_api.dto.order.UpdateCart;
import co.kurrant.app.public_api.dto.order.UpdateCartDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserUtil;
import co.kurrant.app.public_api.service.CartService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository orderCartRepository;
    private final CartDailyFoodRepository cartDailyFoodRepository;
    private final DailyFoodRepository dailyFoodRepository;
    private final QCartItemRepository qOrderCartItemRepository;
    private final QUserSupportPriceHistoryRepository qUserSupportPriceHistoryRepository;
    private final UserUtil userUtil;
    private final CartDailyFoodMapper orderCartDailyFoodMapper;
    private final CartDailyFoodsResMapper cartDailyFoodsResMapper;
    private final DeliveryFeePolicy deliveryFeePolicy;
    private final UserSupportPriceUtil userSupportPriceUtil;

    @Override
    @Transactional
    public Integer saveOrderCart(SecurityUser securityUser, CartDto cartDto) {
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

        // DailyFood 가져오기
        DailyFood dailyFood = dailyFoodRepository.findById(cartDto.getDailyFoodId()).orElseThrow(
                () -> new ApiException(ExceptionEnum.DAILY_FOOD_NOT_FOUND)
        );

        // TODO: 상품마다 주문시간이 다른 경우가 존재하는지 확인
        List<MealInfo> mealInfos = dailyFood.getSpot().getMealInfos();
        MealInfo mealInfo = mealInfos.stream().filter(v -> v.getDiningType().equals(dailyFood.getDiningType()))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        // 주문 시간이 지났는지 확인하기
        LocalDateTime lastOrderTime = LocalDateTime.of(dailyFood.getServiceDate(), mealInfo.getLastOrderTime());
        if(LocalDateTime.now().isAfter(lastOrderTime)) {
            throw new ApiException(ExceptionEnum.LAST_ORDER_TIME_PASSED);
        }
        // 상품이 품절되었는지 확인하기
        if(!dailyFood.getFoodStatus().equals(FoodStatus.SALES)) {
            throw new ApiException(ExceptionEnum.SOLD_OUT);
        }

        // DailyFood가 중복될 경우는 추가하지 않고 count 수만큼 수량 증가 처리
        Optional<CartDailyFood> orderCartDailyFood = cartDailyFoodRepository.findOneByUserAndDailyFood(user, dailyFood);
        if (orderCartDailyFood.isPresent()) {
            orderCartDailyFood.get().updateCount(orderCartDailyFood.get().getCount() + cartDto.getCount());
            return 2;
        }

        // 중복되는 DailyFood가 장바구니에 존재하지 않는다면 추가하기
        CartDailyFood newOrderCartDailyFood = orderCartDailyFoodMapper.toEntity(user, cartDto.getCount(), dailyFood);

        //장바구니에 추가
        cartDailyFoodRepository.save(newOrderCartDailyFood);
        return 1;
    }

    @Override
    @Transactional
    public CartResDto findUserCart(SecurityUser securityUser) {
        //유저정보 가져오기
        User user = userUtil.getUser(securityUser);

        //결과값 저장을 위한 LIST 생성
        MultiValueMap<Spot, CartDailyFood> spotDailyFoodMap = new LinkedMultiValueMap<>();
        List<CartResDto.SpotCarts> spotCartsList = new ArrayList<>();

        // 유저 정보로 카드 정보 불러와서 카트에 담긴 아이템 찾기
        List<CartDailyFood> cartDailyFoods = cartDailyFoodRepository.findAllByUser(user);

        // 장바구니에 담긴 상품이 없다면 null 값 return
        if(cartDailyFoods.isEmpty()) {
            return null;
        }
        // 스팟별로 식단 나누기
        for (CartDailyFood spotDailyFood : cartDailyFoods) {
            spotDailyFoodMap.add(spotDailyFood.getSpot(), spotDailyFood);
        }
        for(Spot spot : spotDailyFoodMap.keySet()) {
            // TODO: Fetch.LAZY 적용시 Spot과 Group가 Proxy이기 때문에 instanceof 사용 불가능함.
            //  현재 CartDailyFood -> Spot -> Group Fetch.EAGER 설정. 추후 수정 필요
            Group group = spot.getGroup();
            // 식사일정(DiningType), 날짜별(serviceDate)로 장바구니 아이템 구분하기
            List<CartDailyFoodDto> cartDailyFoodListDtos = new ArrayList<>();
            MultiValueMap<DiningTypeServiceDate, CartDailyFoodDto.DailyFood> cartDailyFoodDtoMap = new LinkedMultiValueMap<>();
            Set<DiningTypeServiceDate> diningTypeServiceDates = new HashSet<>();
            for (CartDailyFood cartDailyFood : Objects.requireNonNull(spotDailyFoodMap.get(spot))) {
                // 식사일정과 날짜 기준으로 DailyFood 매핑
                DiningTypeServiceDate diningTypeServiceDate = new DiningTypeServiceDate(cartDailyFood.getDailyFood().getServiceDate(), cartDailyFood.getDailyFood().getDiningType());
                diningTypeServiceDates.add(diningTypeServiceDate);
                // CartDailyFood Dto화
                DiscountDto discountDto = DiscountDto.getDiscount(cartDailyFood.getDailyFood().getFood());
                OrderUtil.checkMembershipAndUpdateDiscountDto(user, group, discountDto);
                CartDailyFoodDto.DailyFood dailyFood = cartDailyFoodsResMapper.toDto(cartDailyFood, discountDto);
                cartDailyFoodDtoMap.add(diningTypeServiceDate, dailyFood);
            }
            // ServiceDate의 가장 빠른 날짜와 늦은 날짜 구하기
            PeriodDto periodDto = userSupportPriceUtil.getEarliestAndLatestServiceDate(diningTypeServiceDates);
            // ServiceDate에 해당하는 사용 지원금 리스트 받아오기
            List<UserSupportPriceHistory> userSupportPriceHistories = qUserSupportPriceHistoryRepository.findAllUserSupportPriceHistoryBetweenServiceDate(user, periodDto.getStartDate(), periodDto.getEndDate());
            // 배송비 및 지원금 계산
            for (DiningTypeServiceDate diningTypeServiceDate : diningTypeServiceDates) {
                BigDecimal supportPrice = BigDecimal.ZERO;
                BigDecimal deliveryFee = deliveryFeePolicy.getGroupDeliveryFee(user, group);
                // 사용 가능한 지원금 가져오기
                if(spot instanceof CorporationSpot) {
                    supportPrice = userSupportPriceUtil.getGroupSupportPriceByDiningType(spot, diningTypeServiceDate.getDiningType());
                    // 기존에 사용한 지원금이 있다면 차감
                    BigDecimal usedSupportPrice = userSupportPriceUtil.getUsedSupportPrice(userSupportPriceHistories, diningTypeServiceDate.getServiceDate(), diningTypeServiceDate.getDiningType());
                    supportPrice = supportPrice.subtract(usedSupportPrice);
                }
                CartDailyFoodDto cartDailyFoodDto = CartDailyFoodDto.builder()
                        .serviceDate(DateUtils.format(diningTypeServiceDate.getServiceDate(), "yyyy-MM-dd"))
                        .diningType(diningTypeServiceDate.getDiningType().getDiningType())
                        .supportPrice(supportPrice)
                        .deliveryFee(deliveryFee)
                        .cartDailyFoods(cartDailyFoodDtoMap.get(diningTypeServiceDate))
                        .build();
                cartDailyFoodListDtos.add(cartDailyFoodDto);
            }
            CartResDto.SpotCarts spotCarts = CartResDto.SpotCarts.builder()
                    .spotId(spot.getId())
                    .spotName(spot.getName())
                    .groupName(spot.getGroup().getName())
                    .cartDailyFoodDtoList(cartDailyFoodListDtos)
                    .build();
            spotCartsList.add(spotCarts);
        }

        // 스팟별로 배송시간과 식사일정에 따른 Dto 매핑하기
        return CartResDto.builder()
                .spotCarts(spotCartsList)
                .userPoint(user.getPoint())
                .build();
    }

    @Override
    @Transactional
    public void deleteByCartItemId(SecurityUser securityUser, BigInteger cartDailyFoodId) {
        //cart_id와 food_id가 같은 경우 삭제
        User user = userUtil.getUser(securityUser);

        qOrderCartItemRepository.deleteByUserAndCartDailyFoodId(user, cartDailyFoodId);
    }

    @Override
    @Transactional
    public void deleteAllCartItemByUserId(SecurityUser securityUser) {
        // order__cart_item에서 user_id에 해당되는 항목 모두 삭제
        User user = userUtil.getUser(securityUser);
        List<Cart> cartList = orderCartRepository.findAllByUser(user);
        qOrderCartItemRepository.deleteByCartId(cartList);
    }

    @Override
    @Transactional
    public void updateByDailyFoodId(SecurityUser securityUser, UpdateCartDto updateCartDto) {
        // 유저 정보를 가져온다
        User user = userUtil.getUser(securityUser);
        // 요청한 장바구니 아이템을 유저가 가지고 있는지 검증한다. 아니라면 예외처리
        List<CartDailyFood> cartDailyFoods = cartDailyFoodRepository.findAllByUser(user);
        List<CartDailyFood> selectedCarts = new ArrayList<>();
        for (UpdateCart updateCart : updateCartDto.getUpdateCartList()) {
            CartDailyFood cartDailyFood = cartDailyFoods.stream().filter(v -> v.getId().compareTo(updateCart.getCartItemId()) == 0)
                    .findAny()
                    .orElseThrow(() -> new ApiException(ExceptionEnum.UNAUTHORIZED));
            selectedCarts.add(cartDailyFood);
        }
        // 수량을 업데이트 한다.
        for (UpdateCart updateCart : updateCartDto.getUpdateCartList()) {
            Optional<CartDailyFood> selectedCart = selectedCarts.stream().filter(v -> v.getId().compareTo(updateCart.getCartItemId()) == 0)
                    .findAny();
            selectedCart.ifPresent(cartDailyFood -> cartDailyFood.updateCount(updateCart.getCount()));
        }
    }
}
