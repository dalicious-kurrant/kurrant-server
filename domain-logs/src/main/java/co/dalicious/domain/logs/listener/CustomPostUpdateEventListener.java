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
                if (oldState[i] instanceof Collection<?> oldCollection && newState[i] instanceof Collection<?> newCollection) {

                    Iterator<?> oldIterator = oldCollection.iterator();
                    Iterator<?> newIterator = newCollection.iterator();
                    int index = 0;

                    while (oldIterator.hasNext() && newIterator.hasNext()) {
                        Object oldElement = oldIterator.next();
                        Object newElement = newIterator.next();
                        Field[] fields = oldElement.getClass().getDeclaredFields();

                        for (Field field : fields) {
                            field.setAccessible(true);
                            try {
                                Object oldValue = field.get(oldElement);
                                Object newValue = field.get(newElement);

                                // Check if both values are of type Number (integer, float, double, etc.)
                                if (oldValue instanceof Number && newValue instanceof Number) {
                                    // Convert both numbers to double and compare.
                                    if (!BigDecimal.valueOf(((Number) oldValue).doubleValue()).setScale(2, RoundingMode.HALF_UP)
                                            .equals(BigDecimal.valueOf(((Number) newValue).doubleValue()).setScale(2, RoundingMode.HALF_UP))) {
                                        // Add to logs only if values are numerically different.
                                        String logEntry = hardwareName + " 기기에서 " + entity.getClass().getSimpleName() + " " + event.getId() + "번 " + properties[i] + "[" + index + "]" + "의 " + field.getName() + "값이 " + '"' + oldValue + '"' + "에서 " + '"' + newValue + '"' + "로 변경.";
                                        logs.add(logEntry);
                                        System.out.println(logEntry);
                                    }
                                } else if (!Objects.equals(oldValue, newValue)) {
                                    // If not numbers, compare normally.
                                    String logEntry = hardwareName + " 기기에서 " + entity.getClass().getSimpleName() + " " + event.getId() + "번 " + properties[i] + "[" + index + "]" + "의 " + field.getName() + "값이 " + '"' + oldValue + '"' + "에서 " + '"' + newValue + '"' + "로 변경.";
                                    logs.add(logEntry);
                                    System.out.println(logEntry);
                                }
                            }  catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        index++;
                    }
                }
                // 필드가 Embeddable 속성인지 체크
                else if (oldState[i] != null && newState[i] != null && oldState[i].getClass().getAnnotation(Embeddable.class) != null) {
                    Field[] fields = oldState[i].getClass().getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        try {
                            Object oldValue = field.get(oldState[i]);
                            Object newValue = field.get(newState[i]);

                            // Check if both values are of type Number (integer, float, double, etc.)
                            if (oldValue instanceof Number && newValue instanceof Number) {
                                // Convert both numbers to double and compare.
                                if (!BigDecimal.valueOf(((Number) oldValue).doubleValue()).setScale(2, RoundingMode.HALF_UP)
                                        .equals(BigDecimal.valueOf(((Number) newValue).doubleValue()).setScale(2, RoundingMode.HALF_UP))) {
                                    // Add to logs only if values are numerically different.
                                    String logEntry = hardwareName + " 기기에서 " + entity.getClass().getSimpleName() + " " + event.getId() + "번 " + properties[i] + "의 " + field.getName() + "값이 " + '"' + oldValue + '"' + "에서 " + '"' + newValue + '"' + "로 변경.";
                                    logs.add(logEntry);
                                    System.out.println(logEntry);
                                }
                            } else if (!Objects.equals(oldValue, newValue)) {
                                // If not numbers, compare normally.
                                String logEntry = hardwareName + " 기기에서 " + entity.getClass().getSimpleName() + " " + event.getId() + "번 " + properties[i] + "의 " + field.getName() + "값이 " + '"' + oldValue + '"' + "에서 " + '"' + newValue + '"' + "로 변경.";
                                logs.add(logEntry);
                                System.out.println(logEntry);
                            }
                        }  catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    String logEntry = hardwareName + " 기기에서 " + entity.getClass().getSimpleName() + " " + event.getId() + "번 " + properties[i] + "의 값이 " + '"' + oldState[i] + '"' + "에서 " + '"' + newState[i] + '"' + "로 변경.";
                    logs.add(logEntry);
                    System.out.println(logEntry);
                }
            }
        }
        if(logs.isEmpty()) return;
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

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return PostUpdateEventListener.super.requiresPostCommitHandling(persister);
    }
}
