package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.core.enums.ControllerType;
import co.dalicious.domain.food.dto.FoodGroupDto;
import co.dalicious.domain.food.dto.FoodStatusUpdateDto;
import co.dalicious.domain.food.dto.FoodListDto;
import co.dalicious.domain.food.dto.MakersFoodDetailReqDto;
import co.dalicious.client.core.annotation.ControllerMarker;
import co.dalicious.domain.order.dto.OrderDto;
import co.dalicious.domain.recommend.dto.FoodRecommendDto;
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

    @ControllerMarker(ControllerType.FOOD)
    @Operation(summary = "상품 전체 조회", description = "존재하는 상품을 모두 조회합니다.")
    @GetMapping("/all")
    public ResponseMessage getAllFoodList(@RequestParam(required = false) BigInteger makersId, @RequestParam(required = false) Integer limit, @RequestParam Integer page, OffsetBasedPageRequest pageable) {
        return ResponseMessage.builder()
                .message("모든 상품을 조회했습니다.")
                .data(foodService.getAllFoodList(makersId, limit, page, pageable))
                .build();
    }

    @ControllerMarker(ControllerType.FOOD)
    @Operation(summary = "상품 상세 조회", description = "상품을 상세 조회합니다.")
    @GetMapping("")
    public ResponseMessage getFoodDetail(@RequestParam(name = "foodId") BigInteger foodId, @RequestParam(name = "makersId") BigInteger makersId) {
        return ResponseMessage.builder()
                .message("상품을 조회했습니다.")
                .data(foodService.getFoodDetail(foodId, makersId))
                .build();
    }

    @ControllerMarker(ControllerType.FOOD)
    @Operation(summary = "상품 상태 수정", description = "선택된 상품의 상태를 변경합니다.")
    @PostMapping("/status")
    public ResponseMessage updateFood(@RequestBody List<FoodStatusUpdateDto> foodStatusUpdateDtos) {
        foodService.updateFoodStatus(foodStatusUpdateDtos);
        return ResponseMessage.builder()
                .message("상품을 상태 수정에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.FOOD)
    @Operation(summary = "대량 상품 수정", description = "엑셀로 상품을 대량 수정합니다.")
    @PostMapping("/mass")
    public ResponseMessage updateFoodMass(@RequestBody List<FoodListDto.FoodList> foodListDto) {
        foodService.updateFoodMass(foodListDto);
        return ResponseMessage.builder()
                .message("상품을 수정했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.FOOD)
    @Operation(summary = "상품 수정", description = "상품을 수정합니다.")
    @PatchMapping("")
    public ResponseMessage updateFood(@RequestPart(required = false) List<MultipartFile> files, @RequestPart MakersFoodDetailReqDto contents) throws IOException {
        foodService.updateFood(files, contents);
        return ResponseMessage.builder()
                .message("상품을 수정했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.FOOD)
    @Operation(summary = "엑셀 내보내기용 조회", description = "엑셀 내보내기를 위해 전체 조회")
    @GetMapping("/excels")
    public ResponseMessage getAllFoodForExcel() {
        return ResponseMessage.builder()
                .message("상품을 조회 했습니다.")
                .data(foodService.getAllFoodForExcel())
                .build();
    }

    @ControllerMarker(ControllerType.FOOD)
    @Operation(summary = "상품 그룹을 조회", description = "상품 그룹을 조회")
    @GetMapping("/groups")
    public ResponseMessage getFoodGroups() {
        return ResponseMessage.builder()
                .message("상품 그룹을 조회 했습니다.")
                .data(foodService.getFoodGroups())
                .build();
    }

    @ControllerMarker(ControllerType.FOOD)
    @Operation(summary = "상품 그룹 엑셀 수정", description = "상품 그룹 엑셀 수정")
    @PostMapping("/groups/excel")
    public ResponseMessage excelFoodGroups(@RequestBody List<FoodGroupDto.Request> requests) {
        foodService.excelFoodGroups(requests);
        return ResponseMessage.builder()
                .message("상품 그룹 엑셀 수정에 성공했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.FOOD)
    @Operation(summary = "상품 그룹을 생성", description = "상품 그룹을 생성")
    @PostMapping("/groups")
    public ResponseMessage postFoodGroups(@RequestBody FoodGroupDto.Request request) {
        foodService.postFoodGroup(request);
        return ResponseMessage.builder()
                .message("상품 그룹을 생성 했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.FOOD)
    @Operation(summary = "상품 그룹 엑셀 삭제", description = "상품 그룹 생성 삭제")
    @DeleteMapping("/groups")
    public ResponseMessage excelFoodGroups(@RequestBody OrderDto.IdList ids) {
        foodService.deleteFoodGroups(ids);
        return ResponseMessage.builder()
                .message("상품 그룹을 삭제했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.FOOD)
    @Operation(summary = "상품 추천 그룹 조회", description = "상품 추천 그룹을 조회한다.")
    @GetMapping("/recommends")
    public ResponseMessage getFoodRecommends() {
        return ResponseMessage.builder()
                .data(foodService.getRecommendsFoodGroup())
                .message("상품 추천 그룹을 생성 했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.FOOD)
    @Operation(summary = "상품 추천 그룹 엑셀 수정", description = "상품 추천 그룹을 엑셀 수정한다.")
    @PostMapping("/recommends/excel")
    public ResponseMessage excelFoodRecommends(@RequestBody List<FoodRecommendDto.Response> foodRecommendDtos) {
        foodService.excelRecommendsFoodGroup(foodRecommendDtos);
        return ResponseMessage.builder()
                .message("상품 추천 그룹 엑셀 수정에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.FOOD)
    @Operation(summary = "상품 추천 그룹 생성", description = "상품 추천 그룹을 생성한다.")
    @PostMapping("/recommends")
    public ResponseMessage postFoodRecommends(@RequestBody FoodRecommendDto.Response foodRecommendDto) {
        foodService.postRecommendsFoodGroup(foodRecommendDto);
        return ResponseMessage.builder()
                .message("상품 추천 그룹을 생성 했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.FOOD)
    @Operation(summary = "상품 추천 그룹 삭제", description = "상품 추천 그룹을 삭제한다.")
    @DeleteMapping("/recommends")
    public ResponseMessage deleteFoodRecommends(@RequestBody OrderDto.IdList ids) {
        foodService.deleteFoodRecommends(ids);
        return ResponseMessage.builder()
                .message("상품 추천 그룹을 삭제하였습니다.")
                .build();
    }
}
