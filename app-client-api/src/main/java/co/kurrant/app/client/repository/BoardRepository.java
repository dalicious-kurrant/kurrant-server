package co.kurrant.app.client.repository;

import java.math.BigInteger;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import co.dalicious.domain.board.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, BigInteger> {
  Optional<Board> findOneByName(String boardName);
}
