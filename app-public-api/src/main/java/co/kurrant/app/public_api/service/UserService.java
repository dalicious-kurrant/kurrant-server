package co.kurrant.app.public_api.service;

import co.dalicious.domain.user.dto.OrderDetailDto;
import co.dalicious.domain.user.entity.OrderDetail;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.user.UserOrderDto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;


public interface UserService {
    User findAll();

    OrderDetailDto findOrderByServiceDate(Date startDate, Date endDate);
}
