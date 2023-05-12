package co.dalicious.domain.logs.entity.listener;

import co.dalicious.domain.logs.entity.CustomRevisionEntity;
import co.dalicious.domain.logs.util.NetworkUtils;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

@Component
public class CustomPostUpdateEventListener implements PostUpdateEventListener {
    private static final int ADMIN_PORT = 8888;

    public void newRevision(Object revisionEntity) {
        // Skip if not admin request
        if (!isAdminRequest()) {
            return;
        }
        CustomRevisionEntity customRevisionEntity = (CustomRevisionEntity) revisionEntity;
        String hardwareName = NetworkUtils.getLocalMacAddress();
        customRevisionEntity.setUsername(hardwareName);
    }

    private boolean isAdminRequest() {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return false;
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        int requestPort = request.getServerPort();

        return requestPort == ADMIN_PORT;
    }


    @Override
    public void onPostUpdate(PostUpdateEvent event) {

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
