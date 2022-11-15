package shop.allof.app.adminapi.repository;

import java.math.BigInteger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.allof.domain.banner.entity.Banner;
import shop.allof.domain.banner.enums.BannerType;

public interface BannerRepository extends JpaRepository<Banner, BigInteger> {

  public Page<Banner> findByType(BannerType type, Pageable pageable);
}
