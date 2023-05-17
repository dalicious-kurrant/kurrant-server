package co.dalicious.domain.logs.config;

import co.dalicious.domain.logs.listener.CustomPostDeleteEventListener;
import co.dalicious.domain.logs.listener.CustomPostInsertEventListener;
import co.dalicious.domain.logs.listener.CustomPostUpdateEventListener;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

@Component
@RequiredArgsConstructor
public class HibernateListenerConfigurer {
    private final CustomPostUpdateEventListener customPostUpdateEventListener;
    private final CustomPostInsertEventListener customPostInsertEventListener;
    private final CustomPostDeleteEventListener customPostDeleteEventListener;

    @PersistenceUnit
    private EntityManagerFactory emf;

    @PostConstruct
    protected void init() {
        SessionFactoryImpl sessionFactory = emf.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
        registry.getEventListenerGroup(EventType.POST_INSERT).appendListener(customPostInsertEventListener);
        registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(customPostUpdateEventListener);
        registry.getEventListenerGroup(EventType.POST_DELETE).appendListener(customPostDeleteEventListener);
    }
}
