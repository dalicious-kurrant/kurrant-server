package co.dalicious.domain.board.repository;


import co.dalicious.domain.board.entity.BackOfficeNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface BackOfficeNoticeRepository extends JpaRepository<BackOfficeNotice, BigInteger> {

    BackOfficeNotice findByIdAndIsStatus(BigInteger id, Boolean isStatus);
}
