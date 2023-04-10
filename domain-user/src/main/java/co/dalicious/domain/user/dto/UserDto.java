package co.dalicious.domain.user.dto;

import co.dalicious.domain.user.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Schema(description = "유저 생성 Dto")
@Setter
@Getter
public class UserDto {
    private String email;
    private String password;
    private String phone;
    private String name;
    private Role role;
    private String paymentPassword;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    Date createdDateTime;

    @Builder
    public UserDto(String email, String password, String phone, String name, Role role,
    Date createdDateTime, String paymentPassword) {
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.name = name;
        this.role = role;
        this.createdDateTime = createdDateTime;
        this.paymentPassword = paymentPassword;
    }
}

