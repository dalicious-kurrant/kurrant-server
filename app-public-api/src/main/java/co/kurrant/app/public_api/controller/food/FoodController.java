package co.kurrant.app.public_api.controller.food;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.food.dto.FoodDetailDto;
import co.kurrant.app.public_api.dto.food.DailyFoodDto;
import co.kurrant.app.public_api.service.FoodService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "4. Food")
@RequestMapping(value = "/v1/foods")
@RestController
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @Operation(summary = "식단 불러오기", description = "특정스팟의 원하는 날짜의 식단을 조회한다.")
    @GetMapping("/dailyfoods")
    public ResponseMessage getDailyFood(@RequestParam Integer spotId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectedDate) {
        return ResponseMessage.builder()
                        .data(foodService.getDailyFood(spotId, selectedDate))
                        .message("식단 불러오기에 성공하였습니다.")
                        .build();
    }

    @Operation(summary = "메뉴 상세정보 불러오기", description = "특정 메뉴의 상세정보를 불러온다.")
    @GetMapping("/{foodId}")
    public FoodDetailDto getFoodDetail(@PathVariable Integer foodId){
        return foodService.getFoodDetail(foodId);
    }

}
