package co.kurrant.app.admin_api.repository;

import java.math.BigInteger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import co.dalicious.domain.banner.entity.Banner;
import co.dalicious.domain.banner.enums.BannerType;

public interface BannerRepository extends JpaRepository<Banner, BigInteger> {

  public Page<Banner> findByType(BannerType type, Pageable pageable);
}
