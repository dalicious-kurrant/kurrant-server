package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "유저 취향정보 DTO")
public class UserTasteInfoDto {

    private Integer breakfastCount;
    private Integer midnightSnack;
    private Integer exerciseCount;
    private Integer drinkCount;
    private Integer favoriteCountryFood;
    private Integer allergyInfo;
    private Boolean isBegan;
    private Integer beganLevel;
    private Boolean isProtein;
    private Integer proteinScoop;
    private Integer proteinBarFrequency;
    private Integer proteinDrinkFrequency;


}
