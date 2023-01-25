package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.OrderDailyFoodService;
import co.kurrant.app.public_api.service.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class OrderDailyFoodServiceImpl implements OrderDailyFoodService {
    private final UserUtil userUtil;

    @Override
    public void orderDailyFoods(SecurityUser securityUser, BigInteger spotId) {
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

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
