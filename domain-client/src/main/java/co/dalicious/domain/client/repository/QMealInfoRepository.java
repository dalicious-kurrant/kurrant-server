package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.dto.UpdateSpotDetailRequestDto;
import co.dalicious.system.enums.DiningType;
import com.querydsl.core.types.Path;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static co.dalicious.domain.client.entity.QCorporationMealInfo.corporationMealInfo;
import static co.dalicious.domain.client.entity.QMealInfo.mealInfo;

@Repository
@RequiredArgsConstructor
public class QMealInfoRepository {

    private final JPAQueryFactory queryFactory;

}
