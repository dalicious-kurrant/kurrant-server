package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.client.entity.CorporationSpot;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.SpotRepository;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.order.dto.CartDailyFoodDto;
import co.dalicious.domain.order.dto.OrderItemDailyFoodReqDto;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.system.util.enums.DiningType;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.OrderDailyFoodService;
import co.kurrant.app.public_api.service.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDailyFoodServiceImpl implements OrderDailyFoodService {
    private final UserUtil userUtil;
    private final SpotRepository spotRepository;
    private final QDailyFoodRepository qDailyFoodRepository;
    private final UserSupportPriceUtil userSupportPriceUtil;

    @Override
    public void orderDailyFoods(SecurityUser securityUser, OrderItemDailyFoodReqDto orderItemDailyFoodReqDto, BigInteger spotId) {
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

        // 스팟 정보 가져오기
        Spot spot = spotRepository.findById(spotId).orElseThrow(
                () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
        );
        // 유저가 그 그룹의 스팟에 포함되는지 확인.
        List<UserGroup> userGroups = user.getGroups();
        userGroups.stream().filter(v -> v.getGroup().equals(spot.getGroup()) && v.getClientStatus().equals(ClientStatus.BELONG))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.UNAUTHORIZED));
        // 식사타입(DiningType)과 날짜별(serviceDate) 식사들 가져오기
        List<CartDailyFoodDto> cartDailyFoodDtoList = orderItemDailyFoodReqDto.getCartDailyFoodDtoList();
        List<BigInteger> dailyFoodsId = new ArrayList<>();
        // DailyFood 객체 가져오기
        for (CartDailyFoodDto cartDailyFoodDto : cartDailyFoodDtoList) {
            for (CartDailyFoodDto.DailyFood dailyFood : cartDailyFoodDto.getCartDailyFoods()) {
                dailyFoodsId.add(dailyFood.getDailyFoodId());
            }
        }
        List<DailyFood> dailyFoodList = qDailyFoodRepository.findAllByFoodIds(dailyFoodsId);
        // 프론트에서 제공한 정보와 실제 정보가 일치하는지 확인
        for (CartDailyFoodDto cartDailyFoodDto : cartDailyFoodDtoList) {
            // 1. 유저 사용가능 지원금이 일치하는지 확인
            BigDecimal supportPrice = BigDecimal.ZERO;
            if(spot instanceof CorporationSpot) {
                supportPrice = userSupportPriceUtil.getGroupSupportPriceByDiningType(spot, DiningType.ofCode(Integer.parseInt(cartDailyFoodDto.getDiningType())));
                // 기존에 사용한 지원금이 있다면 차감
//                BigDecimal usedSupportPrice = userSupportPriceUtil.getUsedSupportPrice(userSupportPriceHistories, diningTypeServiceDate.getServiceDate());
//                supportPrice = supportPrice.subtract(usedSupportPrice);
            }
            cartDailyFoodDto.getSupportPrice();
            for (CartDailyFoodDto.DailyFood dailyFood : cartDailyFoodDto.getCartDailyFoods()) {

            }
        }
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
