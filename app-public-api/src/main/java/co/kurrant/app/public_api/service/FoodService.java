package co.kurrant.app.public_api.service;

import co.kurrant.app.public_api.model.SecurityUser;

import java.math.BigInteger;
import java.time.LocalDate;

public interface FoodService {

    Object getDailyFood(SecurityUser securityUser, BigInteger spotId, LocalDate selectedDate);

    Object getFoodDetail(BigInteger foodId, SecurityUser securityUser);
}
