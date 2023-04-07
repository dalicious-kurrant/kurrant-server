package co.dalicious.domain.user.repository;


import co.dalicious.domain.user.entity.UserTasteTestData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface UserTasteTestDataRepository extends JpaRepository<UserTasteTestData, BigInteger> {



}
