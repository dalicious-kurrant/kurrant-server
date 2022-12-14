package co.dalicious.domain.food.repository;


import co.dalicious.domain.food.entity.DailyFood;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;

@Repository
@RequiredArgsConstructor
public class QDailyFoodRepository {

    public final JPAQueryFactory queryFactory;

    public List<DailyFood> getDailyFood(Integer spotId, String selectedDate) {
        return queryFactory
                .selectFrom(dailyFood)
                .fetch();
        //,
        //                        eqCreatedAt(selectedDate)
    }

    private BooleanExpression eqCreatedAt(String selectedDate){
        if(!StringUtils.hasText(selectedDate)) return null;
        else{
            LocalDate date = LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return dailyFood.created.between(date.atStartOfDay().toLocalDate(), LocalDateTime.of(date, LocalTime.MAX).toLocalDate());
        }
    }


}
