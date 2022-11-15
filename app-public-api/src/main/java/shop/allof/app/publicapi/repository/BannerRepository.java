package shop.allof.app.publicapi.repository;

import java.math.BigInteger;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.allof.domain.banner.entity.Banner;

public interface BannerRepository extends JpaRepository<Banner, BigInteger> {

}
