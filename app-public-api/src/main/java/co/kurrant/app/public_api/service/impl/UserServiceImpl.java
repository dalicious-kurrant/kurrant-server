package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.user.dto.OrderDetailDto;
import co.dalicious.domain.user.dto.OrderItemDto;
import co.dalicious.domain.user.entity.Food;
import co.dalicious.domain.user.entity.OrderDetail;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.FoodRepository;
import co.dalicious.domain.user.repository.OrderDetailRepository;
import co.dalicious.domain.user.repository.UserRepository;
import co.kurrant.app.public_api.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final FoodRepository foodRepository;

    public UserServiceImpl(UserRepository userRepository, OrderDetailRepository orderDetailRepository, FoodRepository foodRepository) {
        this.userRepository = userRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.foodRepository = foodRepository;
    }

    @Override
    public User findAll() {
        User user = userRepository.findAll().get(0);
        return user;
    }

    @Override
    public OrderDetailDto findOrderByServiceDate(Date startDate, Date endDate){
        //JWT로 아이디 받기
        OrderDetailDto orderDetailDto = new OrderDetailDto();

        List<OrderItemDto> orderItemDtoList = new ArrayList<>();

        System.out.println(startDate +" startDate, " + endDate +" endDate");
        List<OrderDetail> byServiceDateBetween = orderDetailRepository.findByServiceDateBetween(startDate, endDate);

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

}
