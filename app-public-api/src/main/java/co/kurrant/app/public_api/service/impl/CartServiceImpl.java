package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.client.repository.SpotRepository;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.food.util.FoodUtils;
import co.dalicious.domain.order.dto.*;
import co.dalicious.domain.order.entity.CartDailyFood;
import co.dalicious.domain.order.entity.DailyFoodSupportPrice;
import co.dalicious.domain.order.mapper.CartDailyFoodMapper;
import co.dalicious.domain.order.repository.CartDailyFoodRepository;
import co.dalicious.domain.order.repository.CartRepository;
import co.dalicious.domain.order.repository.QDailyFoodSupportPriceRepository;
import co.dalicious.domain.order.service.DeliveryFeePolicy;
import co.dalicious.domain.order.util.OrderDailyFoodUtil;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.PeriodDto;
import co.kurrant.app.public_api.dto.order.UpdateCart;
import co.kurrant.app.public_api.dto.order.UpdateCartDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.CartService;
import co.kurrant.app.public_api.util.UserUtil;
import exception.ApiException;
import exception.CustomException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository orderCartRepository;
    private final CartDailyFoodRepository cartDailyFoodRepository;
    private final QDailyFoodRepository qDailyFoodRepository;
    private final CartDailyFoodMapper cartDailyFoodMapper;
    private final QDailyFoodSupportPriceRepository qDailyFoodSupportPriceRepository;
    private final UserUtil userUtil;
    private final DeliveryFeePolicy deliveryFeePolicy;
    private final SpotRepository spotRepository;
    private final OrderDailyFoodUtil orderDailyFoodUtil;

    @Override
    @Transactional
    public CartDto.Response saveOrderCart(SecurityUser securityUser, List<CartDto> cartDtoList) {
        // TODO: 주문 시간이 다르면 장바구니가 분리되어야함
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);
        List<BigInteger> dailyFoodIds = new ArrayList<>();
        for (CartDto cartDto : cartDtoList) {
            dailyFoodIds.add(cartDto.getDailyFoodId());
        }

        // DailyFood 가져오기
        List<DailyFood> dailyFoods = qDailyFoodRepository.findAllByDailyFoodIds(dailyFoodIds);

        if (dailyFoods.isEmpty()) {
            throw new ApiException(ExceptionEnum.DAILY_FOOD_NOT_FOUND);
        }

        Spot spot = spotRepository.findById(cartDtoList.get(0).getSpotId()).orElseThrow(
                () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
        );
        MealInfo mealInfo = spot.getMealInfo(dailyFoods.get(0).getDiningType());

        List<CartDailyFood> cartDailyFoods = cartDailyFoodRepository.findAllByUser(user);
        int cartCount = cartDailyFoods.size();
        List<CartDto.DailyFoodCount> dailyFoodCountList = new ArrayList<>();
        for (CartDto cartDto : cartDtoList) {
            DailyFood dailyFood = dailyFoods.stream().filter(v -> v.getId().equals(cartDto.getDailyFoodId()))
                    .findAny()
                    .orElseThrow(() -> new ApiException(ExceptionEnum.DAILY_FOOD_NOT_FOUND));

            if (!spot.getGroup().equals(dailyFood.getGroup())) {
                throw new ApiException(ExceptionEnum.BAD_REQUEST);
            }

            // 주문 가능한 음식인지 확인
            FoodUtils.isAbleToOrderDailyFood(dailyFood, mealInfo, DateUtils.stringToLocalTime(cartDto.getDeliveryTime()));

            FoodCountDto foodCountDto = orderDailyFoodUtil.getRemainFoodCount(dailyFood);
            if (foodCountDto.getRemainCount() < cartDto.getCount()) {
                throw new ApiException(ExceptionEnum.OVER_ITEM_CAPACITY);
            }

            dailyFoodCountList.add(new CartDto.DailyFoodCount(dailyFood.getId(), foodCountDto.getRemainCount()));

            // DailyFood가 중복될 경우는 추가하지 않고 count 수만큼 수량 증가 처리
            Optional<CartDailyFood> optionalCartDailyFood = cartDailyFoods.stream().filter(v -> v.getDailyFood().equals(dailyFood))
                    .findAny();

            if (optionalCartDailyFood.isPresent()) {
                optionalCartDailyFood.get().updateCount(optionalCartDailyFood.get().getCount() + cartDto.getCount());
                if (foodCountDto.getRemainCount() < optionalCartDailyFood.get().getCount()) {
                    throw new ApiException(ExceptionEnum.OVER_ITEM_CAPACITY);
                }
            } else {
                // 중복되는 DailyFood가 장바구니에 존재하지 않는다면 추가하기
                LocalTime deliveryTime = DateUtils.stringToLocalTime(cartDto.getDeliveryTime());
                if(deliveryTime == null || !mealInfo.getDeliveryTimes().contains(deliveryTime)) {
                    throw new CustomException(HttpStatus.BAD_REQUEST, "CE400011", "배송시간을 선택해주세요.");
                }
                CartDailyFood cartDailyFood = new CartDailyFood(user, cartDto.getCount(), dailyFood, spot, deliveryTime);
                cartDailyFoodRepository.save(cartDailyFood);
                cartCount += 1;
            }
        }
        return new CartDto.Response(cartCount, dailyFoodCountList);
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
        if (cartDailyFoods.isEmpty()) {
            return CartResDto.builder()
                    .userPoint(user.getPoint())
                    .spotCarts(spotCartsList)
                    .build();
        }
        // 스팟별로 식단 나누기
        List<DailyFood> dailyFoods = new ArrayList<>();
        for (CartDailyFood spotDailyFood : cartDailyFoods) {
            spotDailyFoodMap.add((Spot) Hibernate.unproxy(spotDailyFood.getSpot()), spotDailyFood);
            dailyFoods.add(spotDailyFood.getDailyFood());
        }
        Map<DailyFood, Integer> dailyFoodCapacityMap = orderDailyFoodUtil.getRemainFoodsCount(dailyFoods);
        for (Spot spot : spotDailyFoodMap.keySet()) {
            Group group = spot.getGroup();
            // 식사일정(DiningType), 날짜별(serviceDate)로 장바구니 아이템 구분하기
            List<CartDailyFoodDto> cartDailyFoodListDtos = new ArrayList<>();
            MultiValueMap<ServiceDiningDto, CartDailyFoodDto.DailyFood> cartDailyFoodDtoMap = new LinkedMultiValueMap<>();
            Set<ServiceDiningDto> serviceDiningDtos = new HashSet<>();

            for (CartDailyFood cartDailyFood : Objects.requireNonNull(spotDailyFoodMap.get(spot))) {
                // 식사일정과 날짜 기준으로 DailyFood 매핑
                DailyFood dailyFood = cartDailyFood.getDailyFood();
                ServiceDiningDto serviceDiningDto = new ServiceDiningDto(dailyFood.getServiceDate(), dailyFood.getDiningType());
                serviceDiningDtos.add(serviceDiningDto);
                // CartDailyFood Dto화
                DiscountDto discountDto = OrderUtil.checkMembershipAndGetDiscountDto(user, group, spot, dailyFood);
                CartDailyFoodDto.DailyFood cartFood = cartDailyFoodMapper.toDto(cartDailyFood, discountDto);
                cartFood.setCapacity(dailyFoodCapacityMap.get(dailyFood));

                //주문마감시간 추가
                String lastOrderTime = null;

                if (dailyFood.getGroup().getMealInfo(dailyFood.getDiningType()).getLastOrderTime() != null){
                    lastOrderTime = dailyFood.getGroup().getMealInfo(dailyFood.getDiningType()).getLastOrderTime().dayAndTimeToStringByDate(dailyFood.getServiceDate());
                }

                if (dailyFood.getFood().getMakers().getMakersCapacity(dailyFood.getDiningType()).getLastOrderTime() != null){
                    lastOrderTime = dailyFood.getFood().getMakers().getMakersCapacity(dailyFood.getDiningType()).getLastOrderTime().dayAndTimeToStringByDate(dailyFood.getServiceDate());
                }
                cartFood.setLastOrderTime(lastOrderTime);
                cartDailyFoodDtoMap.add(serviceDiningDto, cartFood);
            }
            // ServiceDate의 가장 빠른 날짜와 늦은 날짜 구하기
            PeriodDto periodDto = UserSupportPriceUtil.getEarliestAndLatestServiceDate(serviceDiningDtos);
            // ServiceDate에 해당하는 사용 지원금 리스트 받아오기
            List<DailyFoodSupportPrice> userSupportPriceHistories = qDailyFoodSupportPriceRepository.findAllUserSupportPriceHistoryBetweenServiceDate(user, periodDto.getStartDate(), periodDto.getEndDate());
            // 배송비 및 지원금 계산
            for (ServiceDiningDto serviceDiningDto : serviceDiningDtos) {
                BigDecimal supportPrice = BigDecimal.ZERO;
                group = (Group) Hibernate.unproxy(group);
                BigDecimal deliveryFee = deliveryFeePolicy.getGroupDeliveryFee(user, group);
                // 사용 가능한 지원금 가져오기
                spot = (Spot) Hibernate.unproxy(spot);
                if (spot instanceof CorporationSpot) {
                    supportPrice = UserSupportPriceUtil.getUsableSupportPrice(spot, userSupportPriceHistories, serviceDiningDto.getServiceDate(), serviceDiningDto.getDiningType());
                }
                CartDailyFoodDto cartDailyFoodDto = CartDailyFoodDto.builder()
                        .serviceDate(DateUtils.format(serviceDiningDto.getServiceDate(), "yyyy-MM-dd"))
                        .diningType(serviceDiningDto.getDiningType().getDiningType())
                        .supportPrice(supportPrice)
                        .deliveryFee(deliveryFee)
                        .cartDailyFoods(cartDailyFoodDtoMap.get(serviceDiningDto))
                        .build();
                cartDailyFoodListDtos.add(cartDailyFoodDto);
            }
            // 주문날짜가 빠른 순서와 식사일정이 빠른 것 정렬
            cartDailyFoodListDtos = cartDailyFoodListDtos.stream()
                    .sorted(Comparator.comparing((CartDailyFoodDto v) -> DateUtils.stringToDate(v.getServiceDate()))
                            .thenComparing(v -> DiningType.ofString(v.getDiningType()))
                    )
                    .collect(Collectors.toList());
            CartResDto.SpotCarts spotCarts = CartResDto.SpotCarts.builder()
                    .spotId(spot.getId())
                    .spotName(spot.getName())
                    .groupName(spot.getGroup().getName())
                    .phone(getPhoneNumberForOrder(user, spot))
                    // TODO: 스팟 변경시 변경 필요
                    .groupType(GroupDataType.ofClass(spot.getClass()).getCode())
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
        User user = userUtil.getUser(securityUser);

        //담은 장바구니가 유저의 것인지 검증
        CartDailyFood cartDailyFood = cartDailyFoodRepository.findById(cartDailyFoodId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        if (!cartDailyFood.getUser().equals(user)) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }
        // 장바구니 아이템 삭제
        cartDailyFoodRepository.delete(cartDailyFood);
    }

    @Override
    @Transactional
    public void deleteAllSpotCartItemByUserId(SecurityUser securityUser, BigInteger spotId) {
        // order__cart_item에서 user_id에 해당되는 항목 모두 삭제
        User user = userUtil.getUser(securityUser);
        Spot spot = spotRepository.findById(spotId).orElseThrow(
                () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
        );
        List<CartDailyFood> cartList = orderCartRepository.findAllByUserAndSpot(user, spot);
        cartDailyFoodRepository.deleteAll(cartList);
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
            Optional<CartDailyFood> selectedCart = selectedCarts.stream().filter(v -> v.getId().equals(updateCart.getCartItemId()))
                    .findAny();
            selectedCart.ifPresent(cartDailyFood -> cartDailyFood.updateCount(updateCart.getCount()));
        }
    }

    private String getPhoneNumberForOrder(User user, Spot spot) {
        if(spot instanceof MySpot mySpot && mySpot.getPhone() != null) {
            return mySpot.getPhone();
        }
        return user.getPhone();
    }
}
