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
import co.dalicious.domain.order.entity.OrderCart;
import co.dalicious.domain.order.entity.OrderCartItem;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.repository.*;
import co.dalicious.domain.user.entity.User;

import co.dalicious.system.util.DateUtils;
import co.kurrant.app.public_api.dto.order.UpdateCart;
import co.kurrant.app.public_api.dto.order.UpdateCartDto;
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

    @Override
    public List<OrderDetailDto> findOrderByServiceDate(LocalDate startDate, LocalDate endDate){
        List<OrderDetailDto> resultList = new ArrayList<>();
        OrderDetailDto orderDetailDto = new OrderDetailDto();

        List<OrderItemDto> orderItemDtoList = new ArrayList<>();

        List<OrderItem> byServiceDateBetween = qOrderItemRepository.findByServiceDateBetween(startDate, endDate);
        byServiceDateBetween.forEach( x -> {
            orderDetailDto.setId(x.getId());
            orderDetailDto.setServiceDate(DateUtils.format(x.getServiceDate(), "yyyy-MM-dd") );

            Food food = foodRepository.findById(x.getFoodId());

            OrderItemDto orderItemDto = OrderItemDto.builder()
                    .name(food.getName())
                    .diningType(x.getEDiningType())
                    .img(food.getImg())
                    .count(x.getCount())
                    .build();

            orderItemDtoList.add(orderItemDto);
            orderDetailDto.setOrderItemDtoList(orderItemDtoList);
        });
        resultList.add(orderDetailDto);

        return resultList;
    }

    @Override
    public List<CartItemDto> findCartById(HttpServletRequest httpServletRequest) {
        //유저정보 가져오기
        User user = commonService.getUser(httpServletRequest);
        //결과값 저장을 위한 LIST 생성
        List<CartItemDto> result = new ArrayList<>();

        //유저정보로 카드 정보 불러와서 카트에 담긴 아이템 찾기
        List<OrderCartItem> orderCartItems = qOrderCartItemRepository.getItems(qOrderCartRepository.getCartId(user.getId().intValue()));

        System.out.println(orderCartItems.get(0).toString()+"11");

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
        Integer cartId = cart.get(0).getId();
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
    public void saveOrderCart(HttpServletRequest httpServletRequest, OrderCartDto orderCartDto){
        User user = commonService.getUser(httpServletRequest);
        BigInteger id = user.getId();

        List<OrderCart> orderCartId = orderCartRepository.findByUserId(id);

        //UserId로 찾았을때 장바구니가 없다면 생성
        if(orderCartId.isEmpty()){

            User user1 = User.builder()
                    .id(id)
                    .build();

            OrderCart orderCart1 = OrderCart.builder()
                    .userId(user1.getId())
                    .build();
            OrderCart orderCart = orderCartRepository.save(orderCart1);

            //음식을 저장한다.
            Optional<DailyFood> dailyFood = dailyFoodRepository.findById(orderCartDto.getDailyFoodId());
            //정보들을 담아서 INSERT
            OrderCartItem orderCartItem = OrderCartItem.builder()
                    .serviceDate(LocalDate.parse(orderCartDto.getServiceDate(), DateTimeFormatter.ISO_DATE))
                    .diningType(orderCartDto.getDiningType())
                    .count(orderCartDto.getCount())
                    .orderCart(orderCart)
                    .dailyFood(dailyFood.get())
                    .build();
            orderCartItemRepository.save(orderCartItem);
        }else { //장바구니 ID가 있다면 바로 담아준다.
            //dailyFoodId를 담아준다.
            Optional<DailyFood> dailyFood = dailyFoodRepository.findById(orderCartDto.getDailyFoodId());

            //정보들을 담아서 INSERT
            OrderCartItem orderCartItem = OrderCartItem.builder()
                    .serviceDate(LocalDate.parse(orderCartDto.getServiceDate(), DateTimeFormatter.ISO_DATE))
                    .diningType(orderCartDto.getDiningType())
                    .count(orderCartDto.getCount())
                    .orderCart(orderCartId.get(0))
                    .dailyFood(dailyFood.get())
                    .build();
            orderCartItemRepository.save(orderCartItem);
        }
    }
}
