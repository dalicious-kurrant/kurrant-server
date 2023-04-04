package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "유저 기본 정보 Dto")
public class UserDefaultInfo {

    private String birthYear;
    private String birthMonth;
    private String birthDay;
    private Integer gender;

}
