package co.dalicious.domain.logs.listener;

import co.dalicious.client.core.interceptor.holder.RequestContextHolder;
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
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomPostInsertEventListener implements PostInsertEventListener {
    private static final int ADMIN_PORT = 8888;
    private final AdminLogsRepository adminLogsRepository;

    private boolean isAdminRequest() {
        Integer currentPort = RequestContextHolder.getCurrentPort();
        System.out.println("currentPort = " + currentPort);
        return currentPort != null && currentPort == ADMIN_PORT;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if (!isAdminRequest() || event.getEntity() instanceof AdminLogs) {
            return;
        }

        String hardwareName = NetworkUtils.getLocalMacAddress();
        Object entity = event.getEntity();
        Object[] newState = event.getState();
        String[] properties = event.getPersister().getPropertyNames();
        List<String> logs = new ArrayList<>();

        for (int i = 0; i < properties.length; i++) {
            if (newState[i] != null) {
                if (newState[i] instanceof Collection<?> collection) {
                    int index = 0;
                    for (Object element : collection) {
                        if (element.getClass().getAnnotation(Embeddable.class) != null) {
                            Field[] fields = element.getClass().getDeclaredFields();
                            for (Field field : fields) {
                                field.setAccessible(true);
                                try {
                                    String logEntry = hardwareName + " 기기에서 " + entity.getClass().getSimpleName() + " " + event.getId() + "번 " + properties[i] + "[" + index + "]" + "의 " + field.getName() + "가 " + '"' + field.get(element) + '"' + "로 설정.";
                                    logs.add(logEntry);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        index++;
                    }
                }
                else if (newState[i].getClass().getAnnotation(Embeddable.class) != null) {
                    Field[] fields = newState[i].getClass().getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        try {
                            String logEntry = hardwareName + " 기기에서 " + entity.getClass().getSimpleName() + " " + event.getId() + "번 " + properties[i] + "의 " + field.getName() + "가 " + '"' + field.get(newState[i]) + '"' + "로 설정.";
                            logs.add(logEntry);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    // The property has been set, create a log entry
                }
                else {
                    String logEntry = hardwareName + " 기기에서 " + entity.getClass().getSimpleName() + " " + event.getId() + "번 " + properties[i] + "가 " + newState[i] + "로 설정.";
                    logs.add(logEntry);
                }
            }
        }

        if(!logs.isEmpty()) {
            adminLogsRepository.save(AdminLogs.builder()
                    .logType(LogType.CREATE)
                    .controllerType(RequestContextHolder.getCurrentControllerType())
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
