package co.kurrant.app.public_api.controller.user;

import co.dalicious.domain.user.dto.UserDto;
import co.kurrant.app.public_api.service.UserService;
import co.kurrant.app.public_api.service.impl.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("v1/users/me")
    public UserDto userInfo(){
        return UserMapper.INSTANCE.toDto(userService.findByToken());
    }
}
