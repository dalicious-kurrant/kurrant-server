package co.kurrant.app.public_api.controller.user;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.user.dto.SaveDailyReportDto;
import co.dalicious.domain.user.dto.SaveDailyReportFoodReqDto;
import co.dalicious.domain.user.dto.SaveDailyReportReqDto;
import co.dalicious.domain.user.dto.UserPreferenceDto;
import co.kurrant.app.public_api.dto.user.SavePaymentPasswordDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.DailyReportService;
import co.kurrant.app.public_api.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@Tag(name = "b. 유저")
@RequestMapping(value = "/v1/users/me")
@RestController
@RequiredArgsConstructor
public class DailyReportController {
    private final DailyReportService dailyReportService;


    @PostMapping("/preference")
    @Operation(summary = "회원 정보 입력", description = "회원 정보 입력 저장")
    public ResponseMessage userPreferenceSave(Authentication authentication, @RequestBody UserPreferenceDto userPreferenceDto){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        String message = dailyReportService.userPreferenceSave(securityUser, userPreferenceDto);
        return ResponseMessage.builder()
                .message(message)
                .build();
    }

    @GetMapping("/preference/check")
    @Operation(summary = "회원정보 입력 여부")
    public ResponseMessage userPreferenceCheck(Authentication authentication){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(dailyReportService.userPreferenceCheck(securityUser))
                .message("회원정보 입력 여부 조회 성공")
                .build();
    }

    @GetMapping("/country")
    @Operation(summary = "국가 정보 조회", description = "국가정보를 조회한다.")
    public ResponseMessage getCountry(){
        return ResponseMessage.builder()
                .data(dailyReportService.getCountry())
                .message("국가 정보 조회 성공")
                .build();
    }

    @GetMapping("/tags")
    @Operation(summary = "푸드태그 조회", description = "푸드태그 정보를 조회한다.")
    public ResponseMessage getFavoriteCountryFoods(@RequestParam Integer code){
        return ResponseMessage.builder()
                .data(dailyReportService.getFavoriteCountryFoods(code))
                .message("조회 성공!")
                .build();
    }

    @GetMapping("/jobs")
    @Operation(summary = "직종 조회", description = "직종을 조회한다.")
    public ResponseMessage getJobType(@RequestParam Integer category, @RequestParam (required = false) String code){
        return ResponseMessage.builder()
                .data(dailyReportService.getJobType(category, code))
                .message("조회 성공!")
                .build();
    }

    @GetMapping("/preference/foods")
    @Operation(summary = "음식 TestData 조회", description = "음식 테스트 데이터을 조회한다.")
    public ResponseMessage getTestData(){
        return ResponseMessage.builder()
                .data(dailyReportService.getTestData())
                .message("조회 성공!")
                .build();
    }

    @GetMapping("/preference/foods/images")
    @Operation(summary = "회원정보 입력 중 음식 이미지 불러오기", description = "foodId로 음식 이미지를 불러온다.")
    public ResponseMessage getFoodImage(@RequestParam List<BigInteger> foodId){
        return ResponseMessage.builder()
                .data(dailyReportService.getFoodImage(foodId))
                .message("이미지 조회 성공!")
                .build();
    }

    @Tag(name = "식단리포트")
    @PostMapping("/daily/report/me")
    @Operation(summary = "내 식사 추가하기", description = "식단 리포트에 내가 먹은 음식을 추가한다.")
    public ResponseMessage insertMyFood(Authentication authentication, @RequestBody SaveDailyReportDto saveDailyReportDto){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        dailyReportService.insertMyFood(securityUser, saveDailyReportDto);
        return ResponseMessage.builder()
                .message("식단을 추가했습니다.")
                .build();
    }

    @Tag(name = "식단리포트")
    @GetMapping("/daily/report")
    @Operation(summary = "식단 리포트 조회", description = "식단 리포트를 조회한다.")
    public ResponseMessage getReport(Authentication authentication, @RequestParam String date){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(dailyReportService.getReport(securityUser, date))
                .message("식단 조회 성공!")
                .build();
    }

    @Tag(name = "식단리포트")
    @PostMapping("/daily/report/food")
    @Operation(summary = "주문내역을 리포트로 가져온다", description = "유저의 주문 내역을 리포트로 가져온다.")
    public ResponseMessage insertFoodRecord(Authentication authentication, @RequestBody SaveDailyReportFoodReqDto dto){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        dailyReportService.saveDailyReportFood(securityUser, dto);
        return ResponseMessage.builder()
                .message("리포트에 음식을 추가햇습니다.")
                .build();
    }

    @Operation(summary = "알림 설정", description = "알림/마케팅 수신 정보 설정 동의 여부를 변경한다.")
    @PostMapping("/setting/all")
    public ResponseMessage allChangeAlarmSetting(Authentication authentication, Boolean isActive) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        dailyReportService.allChangeAlarmSetting(securityUser, isActive);
        return ResponseMessage.builder()
                .message("마케팅 수신 정보 변경에 성공하였습니다.")
                .build();
    }

    @Tag(name = "식단리포트")
    @DeleteMapping("/daily/report/{reportId}")
    @Operation(summary = "식단리포트 제거", description = "선택한 식단리포트를 제거 한다.")
    public ResponseMessage deleteDailyReport(Authentication authentication, @PathVariable BigInteger reportId){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        String message = dailyReportService.deleteReport(securityUser, reportId);
        return ResponseMessage.builder()
                .message(message)
                .build();
    }

    @Tag(name = "식단리포트")
    @GetMapping("/daily/report/order")
    @Operation(summary = "주문내역 조회", description = "특정 날짜, 특정 식사타입으로 주문 내역 조회")
    public ResponseMessage getOrderByDateAndDiningType(Authentication authentication, @RequestParam String date, @RequestParam Integer diningType){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(dailyReportService.getOrderByDateAndDiningType(securityUser, date, diningType))
                .message("주문내역 조회에 성공했습니다.")
                .build();
    }

    @Tag(name = "식단리포트")
    @GetMapping("/daily/report/history")
    @Operation(summary = "식사 히스토리", description = "식사 히스토리에 필요한 정보를 조회한다.")
    public ResponseMessage getMealHistory(Authentication authentication, @RequestParam String startDate, @RequestParam String endDate){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(dailyReportService.getMealHistory(securityUser, startDate, endDate))
                .message("조회에 성공했습니다.")
                .build();
    }

    @Tag(name = "식단리포트")
    @PostMapping("/daily/report")
    @Operation(summary = "조회된 식사를 리포트에 추가", description = "식단 리포트 조회로 조회한 음식을 식단에 추가한다.")
    public ResponseMessage saveDailyReport(Authentication authentication, @RequestBody SaveDailyReportReqDto saveDailyReportDto){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        dailyReportService.saveDailyReport(securityUser, saveDailyReportDto);
        return ResponseMessage.builder()
                .message("저장에 성공했습니다.")
                .build();
    }
}
