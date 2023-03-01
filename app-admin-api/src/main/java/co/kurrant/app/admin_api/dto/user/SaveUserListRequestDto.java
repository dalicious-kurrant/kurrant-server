package co.kurrant.app.admin_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Schema(description = "변경된 유저정보를 저장 요청 하는 Dto")
public class SaveUserListRequestDto {
    private String password;
    @NotNull
    private String name;
    @NotNull
    private String email;
    private String phone;
    private String role;
    private Integer status;
    private String groupName;
    private Integer point;
    private Boolean marketingAgree;
    private Boolean marketingAlarm;
    private Boolean orderAlarm;

}
