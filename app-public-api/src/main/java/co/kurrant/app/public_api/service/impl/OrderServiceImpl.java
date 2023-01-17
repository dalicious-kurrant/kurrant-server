package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.repository.DailyFoodRepository;
import co.dalicious.domain.food.repository.FoodRepository;
import co.dalicious.domain.order.dto.CartItemDto;
import co.dalicious.domain.order.dto.OrderCartDto;
import co.dalicious.domain.order.dto.OrderDetailDto;
import co.dalicious.domain.order.dto.OrderItemDto;
import co.dalicious.domain.order.entity.OrderCart;
import co.dalicious.domain.order.entity.OrderCartDailyFood;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.mapper.OrderCartDailyFoodMapper;
import co.dalicious.domain.order.mapper.OrderCartDailyFoodResMapper;
import co.dalicious.domain.order.repository.*;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.public_api.dto.order.UpdateCart;
import co.kurrant.app.public_api.dto.order.UpdateCartDto;
import co.kurrant.app.public_api.mapper.order.OrderCartMapper;
import co.kurrant.app.public_api.mapper.order.OrderDetailMapper;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserUtil;
import co.kurrant.app.public_api.service.OrderService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    private final FoodRepository foodRepository;
    private final OrderCartRepository orderCartRepository;
    private final QOrderCartRepository qOrderCartRepository;
    private final OrderCartDailyFoodRepository orderCartDailyFoodRepository;
    private final DailyFoodRepository dailyFoodRepository;
    private final QOrderCartItemRepository qOrderCartItemRepository;
    private final UserUtil userUtil;
    private final OrderDetailMapper orderDetailMapper;
    private final OrderCartDailyFoodMapper orderCartDailyFoodMapper;
    private final OrderCartDailyFoodResMapper orderCartDailyFoodResMapper;

    @Override
    @Transactional
    public Object findOrderByServiceDate(LocalDate startDate, LocalDate endDate){
        List<OrderDetailDto> resultList = new ArrayList<>();
        OrderDetailDto orderDetailDto = new OrderDetailDto();

        List<OrderItemDto> orderItemDtoList = new ArrayList<>();

        List<OrderDailyFood> orderItemList = qOrderDailyFoodRepository.findByServiceDateBetween(startDate, endDate);

        orderItemList.forEach( x -> {
            orderDetailDto.setId(x.getId());
            orderDetailDto.setServiceDate(DateUtils.format(x.getServiceDate(), "yyyy-MM-dd") );

            Optional<Food> food = foodRepository.findOneById(x.getId());

            OrderItemDto orderItemDto = orderDetailMapper.toOrderItemDto(food.get(), x);

            orderItemDtoList.add(orderItemDto);
            orderDetailDto.setOrderItemDtoList(orderItemDtoList);
            resultList.add(orderDetailDto);
        });
        return resultList;
    }

    @Override
    @Transactional
    public Integer saveOrderCart(SecurityUser securityUser, OrderCartDto orderCartDto){
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

        // DailyFood 가져오기
        DailyFood dailyFood = dailyFoodRepository.findById(orderCartDto.getDailyFoodId()).orElseThrow(
                () -> new ApiException(ExceptionEnum.DAILY_FOOD_NOT_FOUND)
        );

        // DailyFood가 중복될 경우는 추가하지 않고 count +1 처리
        Optional<OrderCartDailyFood> orderCartDailyFood = orderCartDailyFoodRepository.findOneByUserAndDailyFood(user, dailyFood);
        if(orderCartDailyFood.isPresent()) {
            orderCartDailyFood.get().updateCount(orderCartDailyFood.get().getCount() + 1);
            return 2;
        }

        // 중복되는 DailyFood가 장바구니에 존재하지 않는다면 추가하기
        OrderCartDailyFood newOrderCartDailyFood = orderCartDailyFoodMapper.toEntity(user, dailyFood, orderCartDto.getCount());

        //장바구니에 추가
        orderCartDailyFoodRepository.save(newOrderCartDailyFood);
        return 1;
    }

    @Override
    @Transactional
    public Object findCartById(SecurityUser securityUser) {
        //유저정보 가져오기
        User user = userUtil.getUser(securityUser);
        //결과값 저장을 위한 LIST 생성
        List<CartItemDto> cartItemDtos = new ArrayList<>();

        //유저정보로 카드 정보 불러와서 카트에 담긴 아이템 찾기
        List<OrderCartDailyFood> orderCartDailyFoods = qOrderCartItemRepository.getItems(qOrderCartRepository.getCartId(user.getId()));

        //지원금 일괄 만원 적용(임시)
        BigDecimal supportPrice = BigDecimal.valueOf(10000);

        //카트에 담긴 아이템들을 결과 LIST에 담아주기
        for (OrderCartDailyFood oc : orderCartDailyFoods){
            Integer price = oc.getDailyFood().getFood().getPrice();
            Double countPrice = Double.valueOf(price); // 할인율을 구하기 위한 용도
            //count가 1이 아니면 가격 * count
            if (oc.getCount() != 1){
                price = price * oc.getCount();
                countPrice = Double.valueOf(price);
            }

            //정책 미확정으로 배송비 보류
            BigDecimal deliveryFee = BigDecimal.valueOf(0);

            //멤버십 할인 가격
            BigDecimal membershipPrice = BigDecimal.valueOf(0);

            if (user.getIsMembership().equals(1)) {
                membershipPrice = BigDecimal.valueOf(price - (price * 80 / 100));
                price = price - membershipPrice.intValue();
            }
            //판매자 할인 가격
            BigDecimal discountPrice = BigDecimal.valueOf(price - (price * 85 / 100));
            //개발 단계에서는 기본할인 + 기간할인 무조건 적용해서 진행
            price = price - discountPrice.intValue();
            //기간 할인 가격
            BigDecimal periodDiscountPrice = BigDecimal.valueOf((price - price * 90 / 100));
            price = price - periodDiscountPrice.intValue();

            //할인율 구하기
            BigDecimal discountRate = BigDecimal.valueOf(( countPrice - (double) Math.abs(price)) / countPrice);


            cartItemDtos.add(orderCartDailyFoodResMapper.toCartItemDto(oc.getId(),oc,price,supportPrice,deliveryFee,membershipPrice,discountPrice,periodDiscountPrice, discountRate));

        }
            //일일지원금과 합계금액 저장
            BigDecimal totalPrice = BigDecimal.valueOf(0);
            for (CartItemDto cartItem : cartItemDtos){
                totalPrice = BigDecimal.valueOf(totalPrice.intValue() + cartItem.getSumPrice());
            }

            if (totalPrice.intValue() <= supportPrice.intValue()){
                supportPrice = totalPrice;
                totalPrice = BigDecimal.valueOf(0);
            } else {
                totalPrice = BigDecimal.valueOf(totalPrice.intValue() - supportPrice.intValue());
            }
            List<Object> result = new ArrayList<>();
            Map<String, Object> priceMaps = new HashMap<>();
            priceMaps.put("totalPrice",totalPrice);
            priceMaps.put("usedSupportPrice",supportPrice);

            result.add(cartItemDtos);
            result.add(priceMaps);

        return result;
    }

    @Override
    @Transactional
    public void deleteByUserId(SecurityUser securityUser) {
        // order__cart_item에서 user_id에 해당되는 항목 모두 삭제
        User user = userUtil.getUser(securityUser);
        List<OrderCart> cartList = orderCartRepository.findAllByUserId(user.getId());
        BigInteger cartId = cartList.get(0).getId();
        qOrderCartItemRepository.deleteByCartId(cartId);
    }

    @Override
    @Transactional
    public void deleteById(SecurityUser securityUser, Integer dailyFoodId) {
        //cart_id와 food_id가 같은 경우 삭제
        User user = userUtil.getUser(securityUser);
        List<OrderCart> cartList = orderCartRepository.findAllByUserId(user.getId());
        qOrderCartItemRepository.deleteByFoodId(cartList.get(0).getId(), dailyFoodId);
    }

    @Override
    @Transactional
    public void updateByFoodId(UpdateCartDto updateCartDto) {
        for (UpdateCart updateCart : updateCartDto.getUpdateCartList()){
            qOrderCartItemRepository.updateByFoodId(updateCart.getCartItemId(),updateCart.getCount());
        }

    }
}
