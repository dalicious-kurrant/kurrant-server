package co.dalicious.domain.logs.listener;

import co.dalicious.client.core.filter.provider.RequestContextHolder;
import co.dalicious.domain.logs.entity.AdminLogs;
import co.dalicious.domain.logs.entity.enums.LogType;
import co.dalicious.domain.logs.repository.AdminLogsRepository;
import co.dalicious.domain.logs.util.NetworkUtils;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomPostDeleteEventListener implements PostDeleteEventListener {
    private static final int ADMIN_PORT = 8888;
    private final AdminLogsRepository adminLogsRepository;

    private boolean isAdminRequest() {
        Integer currentPort = RequestContextHolder.getCurrentPort();
        return currentPort != null && currentPort == ADMIN_PORT;
    }
    @Override
    public void onPostDelete(PostDeleteEvent event) {
        if (!isAdminRequest()) {
            return;
        }

        String hardwareName = NetworkUtils.getLocalMacAddress();
        Object entity = event.getEntity();
        // And you can access the deleted state of the entity:
        Object[] deletedState = event.getDeletedState();
        // You can also access the names of the properties:
        String[] properties = event.getPersister().getPropertyNames();
        List<String> logs = new ArrayList<>();
        // Now you can create log entries
        for (int i = 0; i < properties.length; i++) {
            if (deletedState[i] != null) {
                // The property has been deleted, create a log entry
                String logEntry = hardwareName + " 기기에서 " + entity.getClass().getSimpleName() + " " + event.getId() + "번 " + properties[i] + "가 " + deletedState[i] + "로 삭제.";
                logs.add(logEntry);
                System.out.println(logEntry);
            }
        }

        adminLogsRepository.save(AdminLogs.builder()
                .logType(LogType.DELETE)
                .controllerType(RequestContextHolder.getCurrentControllerType())
                .baseUrl(RequestContextHolder.getCurrentBaseUrl())
                .endPoint(RequestContextHolder.getCurrentEndpoint())
                .entityName(entity.getClass().getSimpleName())
                .userCode(hardwareName)
                .logs(logs)
                .build());
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }
}
