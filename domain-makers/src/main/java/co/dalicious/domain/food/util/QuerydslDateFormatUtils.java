package co.dalicious.domain.food.util;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;

import java.sql.Timestamp;
import java.time.LocalDate;

import static co.dalicious.domain.food.entity.QPresetMakersDailyFood.presetMakersDailyFood;

public class QuerydslDateFormatUtils {

    public static StringTemplate getStringTemplateByTimestamp(DateTimePath<Timestamp> timestampDateTimePath) {
        StringTemplate formattedDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})"
                , timestampDateTimePath
                , ConstantImpl.create("%Y-%m-%d"));
        return formattedDate;
    }
}
