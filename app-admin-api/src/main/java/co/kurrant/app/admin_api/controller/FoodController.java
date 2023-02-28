package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.food.dto.FoodStatusUpdateDto;
import co.dalicious.domain.food.dto.FoodListDto;
import co.dalicious.domain.food.dto.MakersFoodDetailReqDto;
import co.kurrant.app.admin_api.service.FoodService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/foods")
public class FoodController {

    private final FoodService foodService;

    @Operation(summary = "상품 전체 조회", description = "존재하는 상품을 모두 조회합니다.")
    @GetMapping("/all")
    public ResponseMessage getAllFoodList(@RequestParam Integer limit, @RequestParam Integer page, OffsetBasedPageRequest pageable) {
        return ResponseMessage.builder()
                .message("모든 상품을 조회했습니다.")
                .data(foodService.getAllFoodList(limit, page, pageable))
                .build();
    }

    @Operation(summary = "메이커스 별 상품 조회", description = "메이커스 별 상품을 모두 조회합니다.")
    @GetMapping("/makers")
    public ResponseMessage getAllFoodListByMakers(@RequestParam BigInteger makersId) {
        return ResponseMessage.builder()
                .message("모든 상품을 조회했습니다.")
                .data(foodService.getAllFoodListByMakers(makersId))
                .build();
    }

    @Operation(summary = "상품 상세 조회", description = "상품을 상세 조회합니다.")
    @GetMapping("")
    public ResponseMessage getFoodDetail(@RequestParam(name = "foodId") BigInteger foodId, @RequestParam(name = "makersId") BigInteger makersId) {
        return ResponseMessage.builder()
                .message("상품을 조회했습니다.")
                .data(foodService.getFoodDetail(foodId, makersId))
                .build();
    }

    @Operation(summary = "상품 상태 수정", description = "선택된 상품의 상태를 변경합니다.")
    @PostMapping("/status")
    public ResponseMessage updateFood(@RequestBody List<FoodStatusUpdateDto> foodStatusUpdateDtos) {
        foodService.updateFoodStatus(foodStatusUpdateDtos);
        return ResponseMessage.builder()
                .message("상품을 상태 수정에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "대량 상품 수정", description = "엑셀로 상품을 대량 수정합니다.")
    @PostMapping("/mass")
    public ResponseMessage updateFoodMass(@RequestBody List<FoodListDto> foodListDto) {
        foodService.updateFoodMass(foodListDto);
        return ResponseMessage.builder()
                .message("상품을 수정했습니다.")
                .build();
    }

    @Operation(summary = "상품 수정", description = "상품을 수정합니다.")
    @PutMapping("")
    public ResponseMessage updateFood(@RequestPart(required = false) List<MultipartFile> files, @RequestPart MakersFoodDetailReqDto contents) throws IOException {
        foodService.updateFood(files, contents);
        return ResponseMessage.builder()
                .message("상품을 수정했습니다.")
                .build();
    }
}
