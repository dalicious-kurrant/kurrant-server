package co.kurrant.app.public_api.controller.food;

import co.kurrant.app.public_api.dto.food.DailyFoodDto;
import co.kurrant.app.public_api.dto.user.UserInfoDto;
import co.kurrant.app.public_api.service.FoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Tag(name = "3. Food")
@RequestMapping(value = "/v1/foods/")
@RestController
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @Operation(summary = "식단 불러오기", description = "특정스팟의 원하는 날짜의 식단을 조회한다.")
    @GetMapping("/dailyfoods")
    public DailyFoodDto getDailyFood(Integer spotId, Date selectedDate) {
        return foodService.getDailyFood(spotId, selectedDate);
    }


}
