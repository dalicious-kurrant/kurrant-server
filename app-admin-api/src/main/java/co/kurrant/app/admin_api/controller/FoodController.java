package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.food.dto.FoodDeleteDto;
import co.dalicious.domain.food.dto.FoodListDto;
import co.dalicious.domain.food.dto.MakersFoodDetailReqDto;
import co.kurrant.app.admin_api.model.SecurityUser;
import co.kurrant.app.admin_api.service.FoodService;
import co.kurrant.app.admin_api.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/foods")
@CrossOrigin(origins="*", allowedHeaders = "*")
public class FoodController {

    private final FoodService foodService;

    @Operation(summary = "상품 전체 조회", description = "존재하는 상품을 모두 조회합니다.")
    @GetMapping("/all")
    public ResponseMessage getAllFoodList() {
        return ResponseMessage.builder()
                .message("모든 상품을 조회했습니다.")
                .data(foodService.getAllFoodList())
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

    @Operation(summary = "상품 삭제", description = "선택된 상품을 삭제합니다.")
    @DeleteMapping("")
    public ResponseMessage deleteFood(Authentication authentication, @RequestBody FoodDeleteDto foodDeleteDto) {
        UserUtil.securityUser(authentication);
        foodService.deleteFood(foodDeleteDto);
        return ResponseMessage.builder()
                .message("상품을 삭제했습니다.")
                .build();
    }

    @Operation(summary = "대량 상품 수정", description = "엑셀로 상품을 대량 수정합니다.")
    @PostMapping("/mass")
    public ResponseMessage updateFoodMass(Authentication authentication, @RequestBody List<FoodListDto> foodListDto) {
        UserUtil.securityUser(authentication);
        foodService.updateFoodMass(foodListDto);
        return ResponseMessage.builder()
                .message("상품을 수정했습니다.")
                .build();
    }

    @Operation(summary = "상품 수정", description = "상품을 수정합니다.")
    @PutMapping("")
    public ResponseMessage updateFood(Authentication authentication, @RequestBody MakersFoodDetailReqDto foodDetailDto) {
        UserUtil.securityUser(authentication);
        foodService.updateFood(foodDetailDto);
        return ResponseMessage.builder()
                .message("상품을 수정했습니다.")
                .build();
    }
}
