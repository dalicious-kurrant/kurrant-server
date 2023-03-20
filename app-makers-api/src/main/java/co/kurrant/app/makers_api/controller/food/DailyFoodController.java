package co.kurrant.app.makers_api.controller.food;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.DailyFoodService;
import co.kurrant.app.makers_api.util.UserUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "DailyFood")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/makers/dailyFoods")
@RestController
public class DailyFoodController {
    private final DailyFoodService dailyFoodService;

    @GetMapping("")
    public ResponseMessage getDailyFood(Authentication authentication, @RequestParam Map<String, Object> parameter) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(dailyFoodService.getDailyFood(securityUser, parameter))
                .message("식단 현황 조회에 성공하였습니다.")
                .build();
    }

}
