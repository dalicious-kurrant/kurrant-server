package co.kurrant.app.public_api.service.impl;

import co.kurrant.app.public_api.service.OrderDailyFoodService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class OrderDailyFoodServiceImpl implements OrderDailyFoodService {
    @Override
    public void orderDailyFoods() {
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
