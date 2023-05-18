package co.dalicious.domain.logs.listener;

import co.dalicious.client.core.interceptor.holder.RequestContextHolder;
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

        List<String> logs = new ArrayList<>();
        // Now you can create log entries
        String logEntry = hardwareName + " 기기에서 "  + event.getId() + "번 "+ entity.getClass().getSimpleName() + "이 삭제됨.";
        logs.add(logEntry);

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
