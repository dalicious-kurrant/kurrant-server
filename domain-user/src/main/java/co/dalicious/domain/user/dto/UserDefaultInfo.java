package co.dalicious.domain.user.dto;

import co.dalicious.domain.user.entity.enums.BirthPlace;
import co.dalicious.domain.user.entity.enums.JobType;
import co.dalicious.domain.user.entity.enums.Country;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "유저 기본 정보 Dto")
public class UserDefaultInfo {

    @Schema(description = "태어난 년도")
    private String birthYear;
    @Schema(description = "태어난 월")
    private String birthMonth;
    @Schema(description = "태어난 일")
    private String birthDay;
    @Schema(description = "성별")
    private Integer gender;
    @Schema(description = "국적")
    private String country;
    @Schema(description = "직종")
    private JobType jobType;
    @Schema(description = "상세직종")
    private JobType detailJobType;

}
