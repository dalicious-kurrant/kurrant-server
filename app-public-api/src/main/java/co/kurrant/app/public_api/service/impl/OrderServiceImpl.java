package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.repository.FoodRepository;
import co.dalicious.domain.order.dto.OrderCartDto;
import co.dalicious.domain.order.entity.OrderCart;
import co.dalicious.domain.order.entity.OrderCartItem;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.repository.OrderCartItemRepository;
import co.dalicious.domain.order.repository.OrderCartRepository;
import co.dalicious.domain.order.repository.OrderItemRepository;
import co.dalicious.domain.user.dto.OrderDetailDto;
import co.dalicious.domain.user.dto.OrderItemDto;
import co.kurrant.app.public_api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderItemRepository orderItemRepository;
    private final FoodRepository foodRepository;
    private final OrderCartRepository orderCartRepository;
    private final OrderCartItemRepository orderCartItemRepository;

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
    @Transactional
    public void saveOrderCart(HttpServletRequest httpServletRequest, OrderCartDto orderCartDto){
        //User user = commonService.getUser(httpServletRequest);
        //BigInteger id = user.getId();

        BigInteger id = BigInteger.valueOf(1); // 임시로 적용

        OrderCart orderCart1 = OrderCart.builder()
                .userId(id)
                .build();

        orderCartRepository.save(orderCart1);
        OrderCart orderCartId = orderCartRepository.findByUserId(id);

        //Food DB 생성용
        Food createFood = Food.builder()
                .price(10000)
                .name("무야호장 팥붕어빵")
                .description("무야호장의 심혈을 기울인 팥붕")
                .build();
        Food createFood1 = Food.builder()
                .price(10000)
                .name("무야호장 슈크림붕어빵")
                .description("무야호장의 심혈을 기울인 슈붕")
                .build();
        Food createFood2 = Food.builder()
                .price(10000)
                .name("무야호장 피자붕어빵")
                .description("무야호장의 심혈을 기울인 피붕")
                .build();

        foodRepository.save(createFood);
        foodRepository.save(createFood1);
        foodRepository.save(createFood2);

        Food food = Food.builder()
                .id(orderCartDto.getFoodId())
                .build();

        OrderCartItem orderCartItem = OrderCartItem.builder()
                .created(LocalDate.now())
                .serviceDate(orderCartDto.getServiceDate())
                .diningType(orderCartDto.getDiningType())
                .count(orderCartDto.getCount())
                .orderCart(orderCartId)
                .foodId(food)
                .build();
        orderCartItemRepository.save(orderCartItem);

    }
}
