package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, BigInteger> {

    List<OrderDetail> findByServiceDateBetween(Date startDate, Date endDate);

    List<OrderDetail> findAll();
}