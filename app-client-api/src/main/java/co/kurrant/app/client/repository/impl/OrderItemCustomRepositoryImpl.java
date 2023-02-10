package co.kurrant.app.client.repository.impl;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import co.dalicious.domain.order.entity.OrderDetail;
import co.dalicious.domain.order.entity.OrderStatus;
import co.dalicious.domain.order.entity.QOrder;
import co.dalicious.domain.order.entity.QOrderDetail;
import co.kurrant.app.client.dto.StatsDailyRequestDto;
import co.kurrant.app.client.dto.StatsWeeklyDto;
import co.kurrant.app.client.dto.StatsWeeklyRequestDto;
import co.kurrant.app.client.repository.OrderItemCustomRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderItemCustomRepositoryImpl implements OrderItemCustomRepository {

  @PersistenceContext
  private EntityManager em;
  private final JPAQueryFactory queryFactory;

  @Override
  public Page<OrderDetail> findAllBySearchCriteria(StatsDailyRequestDto dto, Pageable pageable) {
    JPAQuery<OrderDetail> q =
        queryFactory.select(QOrderDetail.orderDetail).from(QOrderDetail.orderDetail);
    JPAQuery<Long> qCnt =
        queryFactory.select(QOrderDetail.orderDetail.count()).from(QOrderDetail.orderDetail);

    q.innerJoin(QOrder.order).on(QOrderDetail.orderDetail.order.eq(QOrder.order));
    qCnt.innerJoin(QOrder.order).on(QOrderDetail.orderDetail.order.eq(QOrder.order));

    List<OrderDetail> fOrderItems =
        q.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
    Long total = qCnt.fetchFirst();

    return new PageImpl<>(fOrderItems, pageable, total);
  }

  /**
   * SELECT SUM(`price`) AS `totalOrderAmount`, COUNT(case when
   * `ORDER__ORDER`.`e_order_status`='CANCEL' then 1 end) AS `totalCancelCount`,
   * COUNT(DISTINCT(`ORDER__ORDER`.`orderer_name`)) AS `totalOrdererCount` FROM
   * `ORDER__ORDER_DETAIL` INNER JOIN `ORDER__ORDER` ON `ORDER__ORDER`.`id` =
   * `ORDER__ORDER_DETAIL`.`order__order_id` GROUP BY DATE_FORMAT(`ORDER__ORDER`.`created_datetime`,
   * '%Y%m%d');
   */
  @Override
  public List<StatsWeeklyDto> findAllByWeeklyStats(StatsWeeklyRequestDto dto) {
    DateTemplate<String> formattedDate = Expressions.dateTemplate(String.class,
        "DATE_FORMAT({0}, {1})", QOrder.order.createdDateTime, ConstantImpl.create("%Y-%m-%d"));

    NumberExpression<Integer> cancelTemplate = QOrder.order.orderStatus.when(OrderStatus.CANCELED)
        .then(Integer.valueOf(1)).otherwise(Integer.valueOf(0));

    queryFactory
        .select(Projections.constructor(StatsWeeklyDto.class, QOrderDetail.orderDetail.price.sum(),
            cancelTemplate.count(), QOrder.order.ordererName.countDistinct()))
        .from(QOrderDetail.orderDetail).innerJoin(QOrder.order)
        .on(QOrderDetail.orderDetail.orderId.eq(QOrder.order.id)).groupBy(formattedDate).fetch();

    return null;
  }

}