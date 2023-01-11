package co.kurrant.app.public_api.model;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Getter
public class UserAccount extends org.springframework.security.core.userdetails.User {
    private final co.dalicious.domain.user.entity.User user;

    public UserAccount(co.dalicious.domain.user.entity.User user) {
        super(user.getEmail(),
                (user.getPassword() == null) ?  "NULL" : user.getPassword() ,
                List.of(new SimpleGrantedAuthority(user.getRole().getAuthority())));
        this.user = user;
    }
}
