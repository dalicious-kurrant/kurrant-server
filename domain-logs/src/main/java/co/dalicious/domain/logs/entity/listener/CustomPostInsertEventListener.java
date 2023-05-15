package co.dalicious.domain.logs.entity.listener;

import co.dalicious.client.core.filter.provider.RequestContextHolder;
import co.dalicious.domain.logs.repository.AdminLogsRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomPostInsertEventListener implements PostInsertEventListener {
    private static final int ADMIN_PORT = 8888;
    private final AdminLogsRepository adminLogsRepository;

    private boolean isAdminRequest() {
        Integer currentPort = RequestContextHolder.getCurrentPort();
        return currentPort != null && currentPort == ADMIN_PORT;
    }
    @Override
    public void onPostInsert(PostInsertEvent event) {
        if (!isAdminRequest()) {
            return;
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }
}
