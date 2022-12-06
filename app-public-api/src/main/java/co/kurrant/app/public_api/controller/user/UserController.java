package co.kurrant.app.public_api.controller.user;

import co.dalicious.domain.user.dto.OrderDetailDto;
import co.dalicious.domain.user.dto.OrderItemDto;
import co.dalicious.domain.user.dto.UserDto;
import co.kurrant.app.public_api.service.UserService;
import co.kurrant.app.public_api.service.impl.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("v1/users/me")
    public UserDto userInfo(){
        return UserMapper.INSTANCE.toDto(userService.findAll());
    }

    @GetMapping("v1/users/me/order")
    public OrderDetailDto userOrderbyDate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate){
        return userService.findOrderByServiceDate(startDate, endDate);
    }
}
