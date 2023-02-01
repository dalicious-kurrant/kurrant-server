//package co.dalicious.client.sse;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//@RequiredArgsConstructor
//public class QNotificationRepository {
//    public final JPAQueryFactory jpaQueryFactory;
//
//    public List<Notification> findByUnreadNotification(Long receiverId, NotificationType type ) {
//        return jpaQueryFactory
//                .selectFrom(notification)
//                .where(notification.receiver.id.eq(receiverId), notification.notificationType.eq(type), notification.isRead.eq(false))
//                .fetch();
//    }
//}
