package co.dalicious.domain.banner.repository;

import co.dalicious.domain.banner.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface BannerRepository extends JpaRepository<Banner, BigInteger> {
}