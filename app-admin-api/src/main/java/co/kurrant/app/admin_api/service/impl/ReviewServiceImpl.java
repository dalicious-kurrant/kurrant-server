package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.domain.review.dto.ReviewAdminResDto;
import co.kurrant.app.admin_api.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    @Override
    public ItemPageableResponseDto<ReviewAdminResDto> getAllReviews(Map<String, Object> parameters, Integer limit, Integer page, OffsetBasedPageRequest pageable, LocalDate start, LocalDate end) {
//        BigInteger makersId = parameters.containsKey("makersId")
        return null;
    }
}
