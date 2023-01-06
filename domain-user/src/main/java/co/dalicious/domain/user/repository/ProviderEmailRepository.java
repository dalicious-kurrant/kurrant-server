package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.enums.Provider;
import co.dalicious.domain.user.entity.ProviderEmail;
import co.dalicious.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface ProviderEmailRepository extends JpaRepository<ProviderEmail, BigInteger> {
    Optional<ProviderEmail> findAllByProviderAndEmail(@Size(max = 16) Provider provider, @Size(max = 64) String email);
    List<ProviderEmail> findAllByEmail(@Size(max = 64) String email);
    List<ProviderEmail> findByUser(User user);
}
