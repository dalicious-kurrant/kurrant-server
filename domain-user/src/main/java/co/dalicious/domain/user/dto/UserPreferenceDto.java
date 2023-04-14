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
    private Integer midnightSnackCount;
    @Schema(description = "운동 횟수")
    private Integer exerciseCount;
    @Schema(description = "음주 횟수")
    private Integer drinkCount;
    @Schema(description = "좋아하는 나라 음식")
    private String favoriteCountryFood;
    @Schema(description = "알러지 정보")
    private String allergyInfo;
    @Schema(description = "기타 알러지 정보")
    private String allergyInfoEtc;
    @Schema(description = "비건여부")
    private Boolean isBegan;
    @Schema(description = "비건 정도")
    private Integer veganLevel;
    @Schema(description = "프로틴 섭취여부")
    private Boolean isProtein;
    @Schema(description = "프로틴 바 섭취 빈도")
    private Integer proteinFrequency;
    @Schema(description = "기본 정보")
    private UserDefaultInfo userDefaultInfo;
    @Schema(description = "선호하는 음식 ID")
    private String selectedFoodId;
    @Schema(description = "선호하지 않는 음식 ID")
    private String unselectedFoodId;


}
