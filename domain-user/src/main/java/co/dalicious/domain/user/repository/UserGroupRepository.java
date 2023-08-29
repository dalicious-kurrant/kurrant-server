package co.dalicious.domain.user.repository;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface UserGroupRepository extends JpaRepository<UserGroup, BigInteger> {
    List<UserGroup> findAllByUser(User user);
    Optional<UserGroup> findOneByUserAndGroupAndClientStatus(User user, Group group, ClientStatus clientStatus);
    @EntityGraph(attributePaths = {"group"})
    List<UserGroup> findAllByUserAndClientStatus(User user, ClientStatus clientStatus);
    List<UserGroup> findAllByGroupAndClientStatus(Group group, ClientStatus clientStatus);
    List<UserGroup> findAllByGroup(Group group);
}
