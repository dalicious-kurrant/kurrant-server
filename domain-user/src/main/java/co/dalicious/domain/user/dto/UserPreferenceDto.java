package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "유저 취향정보 DTO")
public class UserPreferenceDto {

    @Schema(description = "아침 식사 횟수")
    private Integer breakfastCount;
    @Schema(description = "야식 횟수")
    private Integer midnightSnack;
    @Schema(description = "운동 횟수")
    private Integer exerciseCount;
    @Schema(description = "음주 횟수")
    private Integer drinkCount;
    @Schema(description = "좋아하는 나라 음식")
    private Integer favoriteCountryFood;
    @Schema(description = "알러지 정보")
    private Integer allergyInfo;
    @Schema(description = "비건여부")
    private Boolean isBegan;
    @Schema(description = "비건 정도")
    private Integer beganLevel;
    @Schema(description = "프로틴 섭취여부")
    private Boolean isProtein;
    @Schema(description = "프로틴 파우더 섭취 빈도")
    private Integer proteinScoop;
    @Schema(description = "프로틴 바 섭취 빈도")
    private Integer proteinBarFrequency;
    @Schema(description = "프로틴 드링크 섭취빈도")
    private Integer proteinDrinkFrequency;
    @Schema(description = "기본 정보")
    private UserDefaultInfo userDefaultInfo;
    @Schema(description = "선호하는 음식 ID")
    private String selectedFoodId;
    @Schema(description = "선호하지 않는 음식 ID")
    private String unselectedFoodId;

}