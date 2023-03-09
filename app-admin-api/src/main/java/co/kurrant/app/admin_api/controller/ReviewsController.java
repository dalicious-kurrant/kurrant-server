package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.admin_api.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@Tag(name = "5. Review")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/reviews")
@RestController
public class ReviewsController {

    public final ReviewService reviewService;

    @Operation(summary = "리뷰 조회", description = "리뷰를 조회 합니다.")
    @GetMapping("/all")
    public ResponseMessage getAllReviews(@RequestParam Map<String, Object> parameters,
                                         @RequestParam Integer limit, @RequestParam Integer page, OffsetBasedPageRequest pageable,
                                         @RequestParam LocalDate start, @RequestParam LocalDate end) {
        return ResponseMessage.builder()
                .message("리뷰를 조회했습니다.")
                .data(reviewService.getAllReviews(parameters, limit, page, pageable, start, end))
                .build();
    }
}
