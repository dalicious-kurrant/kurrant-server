package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface GroupRepository extends JpaRepository<Group, BigInteger> {
}
