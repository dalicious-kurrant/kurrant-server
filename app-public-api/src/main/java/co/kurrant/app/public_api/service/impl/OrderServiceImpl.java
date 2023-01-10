package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.repository.DailyFoodRepository;
import co.dalicious.domain.food.repository.FoodRepository;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.order.dto.CartItemDto;
import co.dalicious.domain.order.dto.OrderCartDto;
import co.dalicious.domain.order.dto.OrderDetailDto;
import co.dalicious.domain.order.dto.OrderItemDto;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderCart;
import co.dalicious.domain.order.entity.OrderCartItem;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.repository.*;
import co.dalicious.domain.user.entity.User;

import co.dalicious.system.util.DateUtils;
import co.kurrant.app.public_api.dto.order.UpdateCart;
import co.kurrant.app.public_api.dto.order.UpdateCartDto;
import co.kurrant.app.public_api.mapper.order.OrderCartItemMapper;
import co.kurrant.app.public_api.mapper.order.OrderCartMapper;
import co.kurrant.app.public_api.mapper.order.OrderDetailMapper;
import co.kurrant.app.public_api.service.CommonService;
import co.kurrant.app.public_api.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderItemRepository orderItemRepository;
    private final QOrderItemRepository qOrderItemRepository;
    private final FoodRepository foodRepository;
    private final OrderCartRepository orderCartRepository;
    private final QOrderCartRepository qOrderCartRepository;
    private final OrderCartItemRepository orderCartItemRepository;
    private final DailyFoodRepository dailyFoodRepository;
    private final QDailyFoodRepository qDailyFoodRepository;
    private final QOrderCartItemRepository qOrderCartItemRepository;
    private final CommonService commonService;
    private final OrderDetailMapper orderDetailMapper;
    private final OrderCartMapper orderCartMapper;
    private final OrderCartItemMapper orderCartItemMapper;

    @Override
    public Object findOrderByServiceDate(LocalDate startDate, LocalDate endDate){
        List<OrderDetailDto> resultList = new ArrayList<>();
        OrderDetailDto orderDetailDto = new OrderDetailDto();

        List<OrderItemDto> orderItemDtoList = new ArrayList<>();

        List<OrderItem> orderItemList = qOrderItemRepository.findByServiceDateBetween(startDate, endDate);

        orderItemList.forEach( x -> {
            orderDetailDto.setId(x.getId());
            orderDetailDto.setServiceDate(DateUtils.format(x.getServiceDate(), "yyyy-MM-dd") );

            Optional<Food> food = foodRepository.findOneById(x.getFoodId());

            OrderItemDto orderItemDto = orderDetailMapper.toOrderItemDto(food.get(), x);

//            OrderItemDto orderItemDto = OrderItemDto.builder()
//                    .name(food.get().getName())
//                    .diningType(x.getEDiningType())
//                    .img(food.get().getImg())
//                    .count(x.getCount())
//                    .build();

            orderItemDtoList.add(orderItemDto);
            orderDetailDto.setOrderItemDtoList(orderItemDtoList);
            resultList.add(orderDetailDto);
        });
        return resultList;
    }

    @Override
    public Object findCartById(HttpServletRequest httpServletRequest) {
        //유저정보 가져오기
        User user = commonService.getUser(httpServletRequest);
        //결과값 저장을 위한 LIST 생성
        List<CartItemDto> result = new ArrayList<>();

        //유저정보로 카드 정보 불러와서 카트에 담긴 아이템 찾기
        List<OrderCartItem> orderCartItems = qOrderCartItemRepository.getItems(qOrderCartRepository.getCartId(user.getId()));

        //카트에 담긴 아이템들을 결과 LIST에 담아주기
        for (OrderCartItem oc : orderCartItems){
            Integer price = oc.getDailyFood().getFood().getPrice();

            //count가 1이 아니면 가격 * count
            if (oc.getCount() != 1){
                price = price * oc.getCount();
            }

            result.add(CartItemDto.builder()
                    .orderCartItem(oc)
                    .price(price)
                    .build());
        }
        return result;
    }

    @Override
    @Transactional
    public void deleteByUserId(HttpServletRequest httpServletRequest) {
        // order__cart_item에서 user_id에 해당되는 항목 모두 삭제
        User user = commonService.getUser(httpServletRequest);
        List<OrderCart> cart = orderCartRepository.findByUserId(user.getId());
        BigInteger cartId = cart.get(0).getId();
        qOrderCartItemRepository.deleteByCartId(cartId);
    }

    @Override
    @Transactional
    public void deleteById(HttpServletRequest httpServletRequest, Integer dailyFoodId) {
        //cart_id와 food_id가 같은 경우 삭제
        User user = commonService.getUser(httpServletRequest);
        List<OrderCart> cart = orderCartRepository.findByUserId(user.getId());
        qOrderCartItemRepository.deleteByFoodId(cart.get(0).getId(), dailyFoodId);
    }

    @Override
    @Transactional
    public void updateByFoodId(HttpServletRequest httpServletRequest, UpdateCartDto updateCartDto) {
        User user = commonService.getUser(httpServletRequest);
        List<OrderCart> cart = orderCartRepository.findByUserId(user.getId());

        for (UpdateCart updateCart : updateCartDto.getUpdateCartList()){
            //dailyFoodId를 담아준다.
            DailyFood dailyFood = DailyFood.builder()
                    .id(updateCart.getDailyFoodId())
                    .build();

            OrderCartItem updateCartItem = OrderCartItem.builder()
                    .orderCart(cart.get(0))
                    .dailyFood(dailyFood)
                    .count(updateCart.getCount())
                    .build();
            qOrderCartItemRepository.updateByFoodId(updateCartItem);
        }

    }

    @Override
    @Transactional
    public Integer saveOrderCart(HttpServletRequest httpServletRequest, OrderCartDto orderCartDto){
        User user = commonService.getUser(httpServletRequest);

        //장바구니가 없다면 장바구니 생성(CartId부여)
       if(!qOrderCartRepository.existsByUserId(user.getId())){
           OrderCart createOrderCart = orderCartMapper.newOrderCart(user.getId());
           orderCartRepository.save(createOrderCart);
       }

        //장바구니에 넣어줄 Item 전처리
        Optional<DailyFood> dailyFoodById = dailyFoodRepository.findById(orderCartDto.getDailyFoodId());

        //DailyFood가 중복될 경우는 추가하지 않고 count +1 처리
        List<OrderCartItem> userCartItemList = qOrderCartItemRepository.getUserCartItemList(user.getId());
        if (!userCartItemList.isEmpty()){
            for (OrderCartItem cartItem : userCartItemList){
                //UserId로 조회한 카트에 담긴 아이템과 새로 넣으려는 아이템을 비교해서 DailyFoodId와 ServiceDate가 같다면 수량만 +1
                if (dailyFoodById.get().getId().equals(cartItem.getId()) &&
                        dailyFoodById.get().getServiceDate().equals(cartItem.getServiceDate())){
                    qOrderCartItemRepository.updateCount(cartItem.getId());
                    return 2;
                }
            }
        }

        Optional<OrderCart> orderCart = qOrderCartRepository.findOneByUserId(user.getId());
        OrderCartItem orderCartItem = orderCartItemMapper.CreateOrderCartItem(dailyFoodById.get(),orderCartDto.getCount(), orderCart.get());

        //장바구니에 추가
        orderCartItemRepository.save(orderCartItem);
        return 1;
    }
}
