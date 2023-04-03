package co.kurrant.app.public_api.mapper;

import co.dalicious.domain.board.entity.Notice;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.PaymentCancelHistory;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.user.dto.PointResponseDto;
import co.dalicious.domain.user.entity.PointHistory;
import co.dalicious.domain.user.entity.enums.PointStatus;
import co.dalicious.system.util.DateUtils;
import exception.ApiException;
import exception.ExceptionEnum;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PointHistoryMapper {

    default PointResponseDto.PointHistoryDto toPointRequestDto(PointHistory pointHistory, List<Reviews> reviewsList, List<Order> orderList, List<Notice> noticeList,
                                                               List<PaymentCancelHistory> cancelHistoryList, BigDecimal leftPoint) {
        PointResponseDto.PointHistoryDto pointRequestDto = new PointResponseDto.PointHistoryDto();

        pointRequestDto.setRewardDate(DateUtils.toISOLocalDate(pointHistory.getCreatedDateTime()));
        pointRequestDto.setPoint(pointHistory.getPoint());
        pointRequestDto.setLeftPoint(leftPoint);
        pointRequestDto.setPointStatus(pointHistory.getPointStatus().getCode());
        getNameAndSetIdsAndMakersName(reviewsList, orderList, noticeList, cancelHistoryList, pointRequestDto, pointHistory);

        return pointRequestDto;
    }

    default void getNameAndSetIdsAndMakersName(List<Reviews> reviewsList, List<Order> orderList, List<Notice> noticeList, List<PaymentCancelHistory> cancelHistoryList,
                                               PointResponseDto.PointHistoryDto pointRequestDto, PointHistory pointHistory) {

        StringBuilder name = new StringBuilder();
        BigInteger id = null;
        String makersName = null;

        if(pointHistory.getPointStatus().equals(PointStatus.REVIEW_REWARD)) {
            Reviews reviews = reviewsList.stream()
                    .filter(r -> pointHistory.getReviewId().equals(r.getId())).findFirst()
                    .orElseThrow(() -> new ApiException(ExceptionEnum.REVIEW_NOT_FOUND));
            OrderItem orderItem = (OrderItem) Hibernate.unproxy(reviews.getOrderItem());
            if(orderItem instanceof  OrderItemDailyFood orderItemDailyFood) {
                Food food = orderItemDailyFood.getDailyFood().getFood();
                makersName = food.getMakers().getName();
                name.append(food.getName());
            }
            id = reviews.getId();
        }

        else if(pointHistory.getPointStatus().equals(PointStatus.USED)) {
            Order order = orderList.stream()
                    .filter(o -> pointHistory.getOrderId().equals(o.getId())).findFirst()
                    .orElseThrow(() -> new ApiException(ExceptionEnum.ORDER_NOT_FOUND));
            List<OrderItem> orderItems = order.getOrderItems();
            DailyFood firstOrder = orderItems.stream().filter(orderItem -> orderItem instanceof OrderItemDailyFood)
                    .map(orderItem -> ((OrderItemDailyFood) orderItem).getDailyFood())
                    .min(Comparator.comparing(DailyFood::getServiceDate))
                    .orElseThrow(() -> new ApiException(ExceptionEnum.DAILY_FOOD_NOT_FOUND));

            makersName = firstOrder.getFood().getMakers().getName();
            name.append(firstOrder.getFood().getName());
            if(orderItems.size() > 1) {
                name.append("외 ").append(orderItems.size() - 1).append("건");
            }
            id = order.getId();
        }

        else if(pointHistory.getPointStatus().equals(PointStatus.EVENT_REWARD)) {
            Notice notice = noticeList.stream()
                    .filter(n -> pointHistory.getBoardId().equals(n.getId())).findFirst()
                    .orElseThrow(() -> new ApiException(ExceptionEnum.NOTICE_NOT_FOUND));

            name.append(notice.getTitle());
            id = notice.getId();
        }

        else if(pointHistory.getPointStatus().equals(PointStatus.CANCEL)) {
            PaymentCancelHistory cancelHistory = cancelHistoryList.stream()
                    .filter(c -> pointHistory.getPaymentCancelHistoryId().equals(c.getId())).findFirst()
                    .orElseThrow(() -> new ApiException(ExceptionEnum.CANCLE_HISTORY_NOT_FOUND));
            OrderItem orderItem = (OrderItem) Hibernate.unproxy(cancelHistory.getOrderItem());
            if(orderItem instanceof OrderItemDailyFood orderItemDailyFood) {
                makersName = orderItemDailyFood.getDailyFood().getFood().getMakers().getName();
                String foodName = orderItemDailyFood.getName();

                name.append(foodName);
            }
            id = cancelHistory.getOrder().getId();
        }

        pointRequestDto.setName(String.valueOf(name));
        pointRequestDto.setContentId(id);
        pointRequestDto.setMakersName(makersName);
    }
}
