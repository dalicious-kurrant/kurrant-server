package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.repository.DailyFoodRepository;
import co.dalicious.domain.food.repository.FoodRepository;
import co.dalicious.domain.order.dto.*;
import co.dalicious.domain.order.entity.Cart;
import co.dalicious.domain.order.entity.CartDailyFood;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.mapper.CartDailyFoodMapper;
import co.dalicious.domain.order.mapper.CartDailyFoodResMapper;
import co.dalicious.domain.order.repository.*;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.public_api.dto.order.UpdateCart;
import co.kurrant.app.public_api.dto.order.UpdateCartDto;
import co.kurrant.app.public_api.mapper.order.OrderDetailMapper;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserUtil;
import co.kurrant.app.public_api.service.OrderService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    private final FoodRepository foodRepository;
    private final CartRepository orderCartRepository;
    private final CartDailyFoodRepository cartDailyFoodRepository;
    private final DailyFoodRepository dailyFoodRepository;
    private final QCartItemRepository qOrderCartItemRepository;
    private final UserUtil userUtil;
    private final CartDailyFoodMapper orderCartDailyFoodMapper;
    private final CartDailyFoodResMapper cartDailyFoodResMapper;

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

    @Override
    @Transactional
    public Integer saveOrderCart(SecurityUser securityUser, CartDto cartDto) {
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

        // DailyFood 가져오기
        DailyFood dailyFood = dailyFoodRepository.findById(cartDto.getDailyFoodId()).orElseThrow(
                () -> new ApiException(ExceptionEnum.DAILY_FOOD_NOT_FOUND)
        );

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
    public Object findUserCart(SecurityUser securityUser) {
        //유저정보 가져오기
        User user = userUtil.getUser(securityUser);

        //결과값 저장을 위한 LIST 생성
        List<CartDailyFoodDto> cartDailyFoodListDtos = new ArrayList<>();
        MultiValueMap<DiningTypeServiceDate, CartDailyFoodDto.DailyFood> cartDailyFoodMap = new LinkedMultiValueMap<>();
        List<DiningTypeServiceDate> diningTypeServiceDates = new ArrayList<>();

        //유저정보로 카드 정보 불러와서 카트에 담긴 아이템 찾기
        List<CartDailyFood> cartDailyFoods = cartDailyFoodRepository.findAllByUser(user);
        for (CartDailyFood cartDailyFood : cartDailyFoods) {
            // CartDailyFood Dto화
            DiscountDto discountDto = DiscountDto.getDiscount(cartDailyFood.getDailyFood().getFood());
            CartDailyFoodDto.DailyFood dailyFood = cartDailyFoodResMapper.toDto(cartDailyFood, discountDto);
            // DiningType과 ServiceDate 기준으로 매핑
            DiningTypeServiceDate diningTypeServiceDate = new DiningTypeServiceDate(cartDailyFood.getDailyFood().getServiceDate(), cartDailyFood.getDailyFood().getDiningType());
            diningTypeServiceDates.add(diningTypeServiceDate);
            cartDailyFoodMap.add(diningTypeServiceDate, dailyFood);
        }

        // DiningType과 ServiceDate 기준에 따라 응답 DTO 생성
        // TODO: 지원금 정책 수정 필요
        for (DiningTypeServiceDate diningTypeServiceDate : diningTypeServiceDates) {
            CartDailyFoodDto cartDailyFoodDto = CartDailyFoodDto.builder()
                    .serviceDate(DateUtils.format(diningTypeServiceDate.getServiceDate(), "yyyy-MM-dd"))
                    .diningType(diningTypeServiceDate.getDiningType().getDiningType())
                    .supportPrice(BigDecimal.ZERO)
                    .cartDailyFoods(cartDailyFoodMap.get(diningTypeServiceDate))
                    .build();
            cartDailyFoodListDtos.add(cartDailyFoodDto);
        }

        // 배송시간과 식사일정에 맞춰서 Dto 매핑하기
        // TODO: 배송비 정책 결정시 수정 필요
        BigDecimal deliveryFee;
        if (user.getIsMembership()) {
            deliveryFee = BigDecimal.ZERO;
        } else {
            deliveryFee = BigDecimal.valueOf(2200L);
        }
        return CartResDto.builder()
                .cartDailyFoodDtoList(cartDailyFoodListDtos)
                .userPoint(user.getPoint())
                .deliveryFee(deliveryFee)
                .build();
    }

    @Override
    @Transactional
    public void deleteById(SecurityUser securityUser, BigInteger cartDailyFoodId) {
        //cart_id와 food_id가 같은 경우 삭제
        User user = userUtil.getUser(securityUser);

        qOrderCartItemRepository.deleteByUserAndCartDailyFoodId(user, cartDailyFoodId);
    }

    @Override
    @Transactional
    public void deleteByUserId(SecurityUser securityUser) {
        // order__cart_item에서 user_id에 해당되는 항목 모두 삭제
        User user = userUtil.getUser(securityUser);
        List<Cart> cartList = orderCartRepository.findAllByUser(user);
        qOrderCartItemRepository.deleteByCartId(cartList);
    }

    @Override
    @Transactional
    public void updateByFoodId(UpdateCartDto updateCartDto) {
        for (UpdateCart updateCart : updateCartDto.getUpdateCartList()) {
            qOrderCartItemRepository.updateByFoodId(updateCart.getCartItemId(), updateCart.getCount());
        }

    }
}
