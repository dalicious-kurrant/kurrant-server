package co.dalicious.domain.logs.entity.listener;

import co.dalicious.client.core.filter.provider.RequestContextHolder;
import co.dalicious.domain.logs.entity.AdminLogs;
import co.dalicious.domain.logs.entity.enums.LogType;
import co.dalicious.domain.logs.repository.AdminLogsRepository;
import co.dalicious.domain.logs.util.NetworkUtils;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

import javax.persistence.Embeddable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        String hardwareName = NetworkUtils.getLocalMacAddress();
        Object entity = event.getEntity();
        // And you can access the new state of the entity:
        Object[] newState = event.getState();
        // You can also access the names of the properties:
        String[] properties = event.getPersister().getPropertyNames();
        List<String> logs = new ArrayList<>();
        // Now you can create log entries
        for (int i = 0; i < properties.length; i++) {
            if (newState[i] != null) {
                if (newState[i].getClass().getAnnotation(Embeddable.class) != null) {
                    Field[] fields = newState[i].getClass().getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        try {
                            String logEntry = hardwareName + " 기기에서 " + entity.getClass().getSimpleName() + " " + event.getId() + "번 " + properties[i] + "의 " + field.getName() + "가 " + '"' + field.get(newState[i]) + '"' + "로 설정.";
                            logs.add(logEntry);
                            System.out.println(logEntry);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    // The property has been set, create a log entry
                    String logEntry = hardwareName + " 기기에서 " + entity.getClass().getSimpleName() + " " + event.getId() + "번 " + properties[i] + "가 " + newState[i] + "로 설정.";
                    logs.add(logEntry);
                }
            }
            adminLogsRepository.save(AdminLogs.builder()
                    .logType(LogType.CREATE)
                    .baseUrl(RequestContextHolder.getCurrentBaseUrl())
                    .endPoint(RequestContextHolder.getCurrentEndpoint())
                    .entityName(entity.getClass().getSimpleName())
                    .userCode(hardwareName)
                    .logs(logs)
                    .build());
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }
}
