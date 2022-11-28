package co.kurrant.app.public_api.service;

import co.dalicious.client.core.exception.ApiException;
import co.dalicious.client.core.exception.ExceptionEnum;
import co.dalicious.domain.user.dto.UserDto;
import co.dalicious.domain.user.entity.Apartment;
import co.dalicious.domain.user.entity.Corporation;
import co.dalicious.domain.user.entity.Role;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.service.impl.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MapStructTest {

    //given
    @Test
    void UserMapper_테스트() {
        Corporation corporation = Corporation.builder()
                .name("N/A")
                .deliveryTime("0")
                .employeeCount(0)
                .build();

        Apartment apartment = Apartment.builder()
                .name("N/A")
                .deliveryTime("0")
                .familyCount(0)
                .build();

        UserDto userDto = UserDto.builder()
                .email("qwe382@naver.com")
                .phone("01094576119")
                .password("alselalsel")
                .name("김민지")
                .role(Role.USER)
                .apartment(apartment)
                .corporation(corporation)
                .build();
        //when
        User user = UserMapper.INSTANCE.toEntity(userDto);

        //then
        Assertions.assertEquals(apartment, user.getApartment());
        Assertions.assertEquals(corporation, user.getCorporation());
    }

}
