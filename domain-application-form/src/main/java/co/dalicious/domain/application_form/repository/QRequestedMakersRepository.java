package co.dalicious.domain.application_form.repository;

import co.dalicious.domain.application_form.entity.RequestedMakers;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static co.dalicious.domain.application_form.entity.QRequestedMakers.requestedMakers;

@Repository
@RequiredArgsConstructor
public class QRequestedMakersRepository {
    private final JPAQueryFactory queryFactory;


}
