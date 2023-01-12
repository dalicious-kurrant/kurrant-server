package co.dalicious.domain.board.repository;

import co.dalicious.domain.board.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, BigInteger> {
}
