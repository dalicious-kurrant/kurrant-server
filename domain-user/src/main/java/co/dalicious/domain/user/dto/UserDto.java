package co.dalicious.domain.user.dto;

import co.dalicious.domain.user.entity.Apartment;
import co.dalicious.domain.user.entity.Corporation;
import co.dalicious.domain.user.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Schema(description = "유저 생성 Dto")
@NoArgsConstructor
@Getter
public class UserDto {
    private String email;
    private String password;
    private String phone;
    private String name;
    private Role role;
    private Corporation corporation;
    private Apartment apartment;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    Date createdDateTime;

    @Builder
    public UserDto(String email, String password, String phone, String name, Role role, Corporation corporation, Apartment apartment,
    Date createdDateTime) {
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.name = name;
        this.role = role;
        this.corporation = corporation;
        this.apartment = apartment;
        this.createdDateTime = createdDateTime;
    }
}

