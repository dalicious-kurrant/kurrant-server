package co.dalicious.client.sse;

import co.dalicious.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.*;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserAndTypeAndIsRead(User user, NotificationType type, boolean isRead);
}
