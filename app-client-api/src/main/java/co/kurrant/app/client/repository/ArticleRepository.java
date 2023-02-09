package co.kurrant.app.client.repository;

import java.math.BigInteger;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import co.dalicious.domain.board.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, BigInteger> {
  Page<Article> findAllByBoardId(BigInteger boardId, Pageable pageable);

  Optional<Article> findOneById(BigInteger articleId);
}
