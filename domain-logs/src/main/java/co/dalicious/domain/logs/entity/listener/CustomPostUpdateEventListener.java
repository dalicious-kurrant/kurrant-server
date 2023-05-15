package co.dalicious.domain.logs.entity.listener;

import co.dalicious.client.core.filter.provider.RequestPortHolder;
import co.dalicious.domain.logs.util.NetworkUtils;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CustomPostUpdateEventListener implements PostUpdateEventListener {
    private static final int ADMIN_PORT = 8888;

    public void newRevision(Object revisionEntity) {
        // Skip if not admin request
        if (!isAdminRequest()) {
            return;
        }
    }

    private boolean isAdminRequest() {
        Integer currentPort = RequestPortHolder.getCurrentPort();
        return currentPort != null && currentPort == ADMIN_PORT;
    }


    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        String hardwareName = NetworkUtils.getLocalMacAddress();
        Object entity = event.getEntity();
        // And you can access the old and new state of the entity:
        Object[] oldState = event.getOldState();
        Object[] newState = event.getState();
        // You can also access the names of the properties:
        String[] properties = event.getPersister().getPropertyNames();
        // Now you can compare the old and new state and create log entries
        for (int i = 0; i < properties.length; i++) {
            if (!Objects.equals(oldState[i], newState[i])) {
                // The property has changed, create a log entry
                String logEntry = hardwareName + " 기기에서 속성명 " + properties[i] + "가 " + oldState[i] + "에서 " + newState[i] + "로 변경되었습니다.";
                // Now you can save this logEntry somewhere
                System.out.println(logEntry);  // For testing, just print it
            }
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return PostUpdateEventListener.super.requiresPostCommitHandling(persister);
    }
}
