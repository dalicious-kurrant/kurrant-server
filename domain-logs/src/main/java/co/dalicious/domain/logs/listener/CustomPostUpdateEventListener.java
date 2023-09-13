package co.dalicious.domain.logs.listener;

import co.dalicious.client.core.interceptor.holder.RequestContextHolder;
import co.dalicious.domain.logs.entity.AdminLogs;
import co.dalicious.domain.logs.entity.enums.LogType;
import co.dalicious.domain.logs.repository.AdminLogsRepository;
import co.dalicious.domain.logs.util.NetworkUtils;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

import javax.persistence.Embeddable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class CustomPostUpdateEventListener implements PostUpdateEventListener {
    private static final int ADMIN_PORT = 8888;
    private final AdminLogsRepository adminLogsRepository;

    private boolean isAdminRequest() {
        Integer currentPort = RequestContextHolder.getCurrentPort();
        return currentPort != null && currentPort == ADMIN_PORT;
    }


    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        // 백오피스가 아니라면 로그를 저장하지 않음
        if (!isAdminRequest()) return;

        String hardwareName = NetworkUtils.getLocalMacAddress();
        Object entity = event.getEntity();
        Object[] oldState = event.getOldState();
        Object[] newState = event.getState();
        String[] properties = event.getPersister().getPropertyNames();
        List<String> logs = new ArrayList<>();
        for (int i = 0; i < properties.length; i++) {
            if (!Objects.equals(oldState[i], newState[i]) && !properties[i].equals("updatedDateTime")) {
                if (newState[i] instanceof Collection<?> newCollection) {
                    if (oldState[i] instanceof Collection<?> oldCollection) {
                        Iterator<?> oldIterator = oldCollection.iterator();
                        Iterator<?> newIterator = newCollection.iterator();
                        int index = 0;

                        while (oldIterator.hasNext() && newIterator.hasNext()) {
                            Object oldElement = oldIterator.next();
                            Object newElement = newIterator.next();
                            processObjectForLogging(entity, oldElement, newElement, event, properties, i, index, hardwareName, logs);
                            index++;
                        }
                    }
                } else if (oldState[i] != null && newState[i] != null && oldState[i].getClass().getAnnotation(Embeddable.class) != null) {
                    processObjectForLogging(entity, oldState[i], newState[i], event, properties, i, null, hardwareName, logs);
                } else {
                    String logEntry = generateLogEntry(entity, event, properties[i], null, null, oldState[i], newState[i], hardwareName);
                    logs.add(logEntry);
                }
            }
        }

        if (!logs.isEmpty()) {
            adminLogsRepository.save(AdminLogs.builder()
                    .logType(LogType.UPDATE)
                    .controllerType(RequestContextHolder.getCurrentControllerType())
                    .baseUrl(RequestContextHolder.getCurrentBaseUrl())
                    .endPoint(RequestContextHolder.getCurrentEndpoint())
                    .entityName(entity.getClass().getSimpleName())
                    .userCode(hardwareName)
                    .logs(logs)
                    .build());
        }
    }

    private void processObjectForLogging(Object entity, Object oldState, Object newState, PostUpdateEvent event, String[] properties, int propertyIndex, Integer collectionIndex, String hardwareName, List<String> logs) {
        if (oldState instanceof LocalDate || oldState instanceof LocalTime || oldState instanceof LocalDateTime) {
            if (!oldState.equals(newState)) {
                String logEntry = generateLogEntry(entity, event, properties[propertyIndex], properties[propertyIndex], collectionIndex, oldState, newState, hardwareName);
                logs.add(logEntry);
                System.out.println(logEntry);
            }
        }
        else {
            Field[] fields = oldState.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.getDeclaringClass().getName().startsWith("co.dalicious")) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    Object oldValue = field.get(oldState);
                    Object newValue = field.get(newState);

                    if (oldValue instanceof Number && newValue instanceof Number) {
                        BigDecimal oldBigDecimal = BigDecimal.valueOf(((Number) oldValue).doubleValue()).stripTrailingZeros();
                        BigDecimal newBigDecimal = BigDecimal.valueOf(((Number) newValue).doubleValue()).stripTrailingZeros();
                        if (!oldBigDecimal.equals(newBigDecimal)) {
                            String logEntry = generateLogEntry(entity, event, properties[propertyIndex], field.getName(), collectionIndex, oldValue, newValue, hardwareName);
                            logs.add(logEntry);
                        }
                    } else if (!Objects.equals(oldValue, newValue)) {
                        String logEntry = generateLogEntry(entity, event, properties[propertyIndex], field.getName(), collectionIndex, oldValue, newValue, hardwareName);
                        logs.add(logEntry);
                        System.out.println(logEntry);
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String generateLogEntry(Object entity, PostUpdateEvent event, String propertyName, String fieldName, Integer collectionIndex, Object oldValue, Object newValue, String hardwareName) {
        StringBuilder sb = new StringBuilder();
        sb.append(hardwareName).append(" 기기에서 ")
                .append(entity.getClass().getSimpleName()).append(" ")
                .append(event.getId()).append("번 ")
                .append(propertyName);
        if (collectionIndex != null) {
            sb.append("[").append(collectionIndex).append("]");
        }
        if (fieldName != null) {
            sb.append("의 ").append(fieldName);
        }
        sb.append(" 값이 ").append('"').append(oldValue == null ? null : oldValue.toString()).append('"')
                .append("에서 ").append('"').append(newValue == null ? null : newValue.toString()).append('"')
                .append("로 변경.");
        return sb.toString();
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
