package co.kurrant.app.admin_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.domain.review.dto.ReviewAdminResDto;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Map;

public interface ReviewService {

    ItemPageableResponseDto<ReviewAdminResDto> getAllReviews(@RequestParam Map<String, Object> parameters, Integer limit, Integer page, OffsetBasedPageRequest pageable);
}
