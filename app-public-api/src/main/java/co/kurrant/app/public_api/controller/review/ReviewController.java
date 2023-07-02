package co.kurrant.app.public_api.controller.review;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.review.dto.ReviewReqDto;
import co.dalicious.domain.review.dto.ReviewUpdateReqDto;
import co.kurrant.app.public_api.dto.order.IdDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.ReviewService;
import co.kurrant.app.public_api.service.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.List;

@Tag(name = "리뷰")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "v1/users/me/reviews")
public class ReviewController {

    public final ReviewService reviewService;

    // 수정 필요. dailyfood id로 검색할 경우 product가 들어욌을 때 애매
    @Operation(summary = "리뷰 작성", description = "리뷰 작성 하기")
    @PostMapping("")
    public ResponseMessage createReview(Authentication authentication,
                                        @RequestPart(required = false) List<MultipartFile> fileList,
                                        @RequestPart ReviewReqDto reviewDto) throws IOException {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        reviewService.createReview(securityUser, reviewDto, fileList);
        return ResponseMessage.builder()
                .message("리뷰 작성을 완료했습니다.")
                .build();
    }

    @Operation(summary = "리뷰 작성 상품 조회", description = "리뷰 작성 가능 상품 조회")
    @GetMapping("/items")
    public ResponseMessage getOrderItemForReview(Authentication authentication) throws ParseException {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("리뷰 작성이 가능한 상품을 조회했습니다.")
                .data(reviewService.getOrderItemForReview(securityUser))
                .build();
    }

    @Operation(summary = "리뷰 조회", description = "작성한 리뷰 조회")
    @GetMapping("")
    public ResponseMessage getReviewsForUser(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("작성한 리뷰를 불러왔습니다.")
                .data(reviewService.getReviewsForUser(securityUser))
                .build();
    }

    @Operation(summary = "리뷰 수정", description = "작성한 리뷰 수정")
    @PatchMapping("/update")
    public ResponseMessage updateReviews(Authentication authentication, @RequestParam BigInteger id,
                                         @RequestPart(required = false) List<MultipartFile> fileList,
                                         @Valid @RequestPart ReviewUpdateReqDto updateReqDto) throws IOException {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        reviewService.updateReviews(securityUser, fileList, updateReqDto, id);
        return ResponseMessage.builder()
                .message("리뷰를 수정했습니다.")
                .build();
    }

    @Operation(summary = "리뷰 삭제", description = "작성한 리뷰 삭제")
    @PatchMapping("/delete")
    public ResponseMessage deleteReviews(Authentication authentication, @RequestBody IdDto idDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        reviewService.deleteReviews(securityUser, idDto.getId());
        return ResponseMessage.builder()
                .message("리뷰를 삭제했습니다.")
                .build();
    }

    @Operation(summary = "리뷰 별점 조회", description = "리뷰의 별점 갯수를 조회한다.")
    @GetMapping("/satisfaction")
    public ResponseMessage reviewStarCount(@RequestParam BigInteger dailyFoodId){
        return ResponseMessage.builder()
                .data(reviewService.reviewStarCount(dailyFoodId))
                .message("리뷰 별점 갯수 조회")
                .build();
    }
}
