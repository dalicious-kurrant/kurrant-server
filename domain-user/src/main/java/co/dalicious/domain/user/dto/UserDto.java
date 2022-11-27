package co.dalicious.domain.user.dto;

import co.dalicious.domain.group.entity.ClientApartment;
import co.dalicious.domain.group.entity.ClientCorporation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "유저 생성 Dto")
@NoArgsConstructor
@Getter
public class UserDto {
    String email;
    String password;
    String phone;
    String name;
    ClientCorporation corporation;
    ClientApartment apartment;

    @Builder
    public UserDto(String email, String password, String phone, String name, ClientCorporation corporation, ClientApartment apartment) {
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.name = name;
        this.corporation = corporation;
        this.apartment = apartment;
    }
}

