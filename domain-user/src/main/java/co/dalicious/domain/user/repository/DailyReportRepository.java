package co.dalicious.domain.user.repository;


import co.dalicious.domain.user.entity.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface DailyReportRepository extends JpaRepository<DailyReport, BigInteger> {


}
