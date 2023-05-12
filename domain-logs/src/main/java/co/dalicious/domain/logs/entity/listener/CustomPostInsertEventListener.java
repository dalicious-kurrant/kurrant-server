package co.dalicious.domain.logs.entity.listener;

import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

@Component
public class CustomPostInsertEventListener implements PostInsertEventListener {
    @Override
    public void onPostInsert(PostInsertEvent event) {
        // Your custom logic here
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }
}
