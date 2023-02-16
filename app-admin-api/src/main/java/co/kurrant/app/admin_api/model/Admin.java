package co.kurrant.app.admin_api.model;

import co.dalicious.domain.user.entity.enums.Role;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class Admin {

    private String username;
    private String password;
    private Role role = Role.ADMIN;

    public Admin(@NotBlank() String username, @NotBlank() String password){
        this.username = username;
        this.password = password;
    }
}
