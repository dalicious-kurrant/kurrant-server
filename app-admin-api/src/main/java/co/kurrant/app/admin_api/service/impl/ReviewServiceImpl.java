package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.MakersRepository;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.QOrderItemDailyFood;
import co.dalicious.domain.order.entity.QOrderItemDailyFoodGroup;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.domain.order.repository.QOrderItemRepository;
import co.dalicious.domain.review.dto.ReviewAdminResDto;
import co.dalicious.domain.review.entity.Comments;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.review.repository.QReviewRepository;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.admin_api.service.ReviewService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final QReviewRepository qReviewRepository;
    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    private final MakersRepository makersRepository;
    @Override
    @Transactional(readOnly = true)
    public ItemPageableResponseDto<ReviewAdminResDto> getAllReviews(Map<String, Object> parameters, Integer limit, Integer page, OffsetBasedPageRequest pageable, LocalDate start, LocalDate end) {
        BigInteger makersId = !parameters.containsKey("makersId") || parameters.get("makersId") == null ? null : BigInteger.valueOf(Integer.parseInt(String.valueOf(parameters.get("makersId"))));
        BigInteger orderItemId = !parameters.containsKey("orderItemId") || parameters.get("orderItemId") == null ? null : BigInteger.valueOf(Integer.parseInt(String.valueOf(parameters.get("orderItemId"))));
        String orderItemName = !parameters.containsKey("orderItemName") || parameters.get("orderItemName") == null ? null : String.valueOf(parameters.get("orderItemName"));
        String writer = !parameters.containsKey("writer") || parameters.get("writer") == null ? null : String.valueOf(parameters.get("writer"));
        Boolean isMakersComment = !parameters.containsKey("isMakersComment") || parameters.get("isMakersComment") == null ? null : Boolean.valueOf(String.valueOf(parameters.get("isMakersComment")));
        Boolean isAdminComment = !parameters.containsKey("isAdminComment") || parameters.get("isAdminComment") == null ? null : Boolean.valueOf(String.valueOf(parameters.get("isAdminComment")));
        Boolean isReport = !parameters.containsKey("isReport") || parameters.get("isReport") == null ? null : Boolean.valueOf(String.valueOf(parameters.get("isReport")));

        List<OrderItemDailyFood> orderItemDailyFoodList = qOrderDailyFoodRepository.findAllByServiceDateBetweenStartAndEnd(start, end);
        List<OrderItem> orderItemList = orderItemDailyFoodList.stream().map(orderItemDailyFood -> (OrderItem) orderItemDailyFood).toList();
        List<Reviews> reviewsList = qReviewRepository.findAllByFilter(makersId, orderItemId, orderItemName, writer, orderItemList, isReport);
        List<Comments> commentsList =

        List<ReviewAdminResDto.ReviewList> reviewDtoList = new ArrayList<>();


        for(Reviews reviews : reviewsList) {

        }

        return null;
    }
}
