package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.repository.FoodRepository;
import co.dalicious.domain.order.dto.CartItemDto;
import co.dalicious.domain.order.dto.OrderCartDto;
import co.dalicious.domain.order.dto.OrderDetailDto;
import co.dalicious.domain.order.dto.OrderItemDto;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderCart;
import co.dalicious.domain.order.entity.OrderCartItem;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.repository.OrderCartItemRepository;
import co.dalicious.domain.order.repository.OrderCartRepository;
import co.dalicious.domain.order.repository.OrderItemRepository;
import co.dalicious.domain.order.repository.QOrderCartItemRepository;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.order.UpdateCartDto;
import co.kurrant.app.public_api.service.CommonService;
import co.kurrant.app.public_api.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderItemRepository orderItemRepository;
    private final FoodRepository foodRepository;
    private final OrderCartRepository orderCartRepository;
    private final OrderCartItemRepository orderCartItemRepository;
    private final QOrderCartItemRepository qOrderCartItemRepository;
    private final CommonService commonService;

    @Override
    public OrderDetailDto findOrderByServiceDate(Date startDate, Date endDate){
        //JWT로 아이디 받기
        OrderDetailDto orderDetailDto = new OrderDetailDto();

        List<OrderItemDto> orderItemDtoList = new ArrayList<>();

        System.out.println(startDate +" startDate, " + endDate +" endDate");
        List<OrderItem> byServiceDateBetween = orderItemRepository.findByServiceDateBetween(startDate, endDate);

        byServiceDateBetween.forEach( x -> {
            orderDetailDto.setId(x.getId());
            orderDetailDto.setServiceDate(x.getServiceDate());

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
        return orderDetailDto;
    }

    @Override
    public List<CartItemDto> findCartById(HttpServletRequest httpServletRequest) {
        //유저정보 가져오기
        User user = commonService.getUser(httpServletRequest);
        //결과값 저장을 위한 LIST 생성
        List<CartItemDto> result = new ArrayList<>();

        //유저정보로 카드 정보 불러와서 카트에 담긴 아이템 찾기
        List<OrderCartItem> orderCartItems = orderCartItemRepository.getItems(Collections.singleton(orderCartRepository.getCartId(user.getId())));

        //카트에 담긴 아이템들을 결과 LIST에 담아주기
        for (OrderCartItem oc : orderCartItems){
            Integer price = oc.getFoodId().getPrice();
            //count가 1이 아니면 가격 * count
            if (oc.getCount() != 1){
                price = price * oc.getCount();
            }

            result.add(CartItemDto.builder()
                    .orderCartItem(oc)
                    .price(price)
                    .build());
        }
        System.out.println(orderCartItems.size() + "결과 size 확인");
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
    public void deleteById(HttpServletRequest httpServletRequest, Integer foodId) {
        //cart_id와 food_id가 같은 경우 삭제
        User user = commonService.getUser(httpServletRequest);
        List<OrderCart> cart = orderCartRepository.findByUserId(user.getId());
        qOrderCartItemRepository.deleteByFoodId(cart.get(0).getId(), foodId);
    }

    @Override
    @Transactional
    public void updateByFoodId(HttpServletRequest httpServletRequest, UpdateCartDto updateCartDto) {
        User user = commonService.getUser(httpServletRequest);
        List<OrderCart> cart = orderCartRepository.findByUserId(user.getId());

        //FoodId를 담아준다.
        Food food = Food.builder()
                .id(updateCartDto.getFoodId())
                .build();

        OrderCartItem updateCartItem = OrderCartItem.builder()
                                       .orderCart(cart.get(0))
                                        .foodId(food)
                                        .count(updateCartDto.getCount())
                                        .build();

        qOrderCartItemRepository.updateByFoodId(updateCartItem);
    }

    @Override
    @Transactional
    public void saveOrderCart(HttpServletRequest httpServletRequest, OrderCartDto orderCartDto){
        User user = commonService.getUser(httpServletRequest);
        BigInteger id = user.getId();

        List<OrderCart> orderCartId = orderCartRepository.findByUserId(id);

        //UserId로 찾았을때 장바구니가 없다면 생성
        if(orderCartId.isEmpty()){
            OrderCart orderCart1 = OrderCart.builder()
                    .userId(id)
                    .build();
            orderCartRepository.save(orderCart1);
        }
        //FoodId를 담아준다.
        Food food = Food.builder()
                .id(orderCartDto.getFoodId())
                .build();
        //정보들을 담아서 INSERT
        OrderCartItem orderCartItem = OrderCartItem.builder()
                .serviceDate(orderCartDto.getServiceDate())
                .diningType(orderCartDto.getDiningType())
                .count(orderCartDto.getCount())
                .orderCart(orderCartId.get(0))
                .foodId(food)
                .build();
        orderCartItemRepository.save(orderCartItem);



    }
}
