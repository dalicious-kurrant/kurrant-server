package co.dalicious.client.alarm.util;

import co.dalicious.client.alarm.dto.BatchAlarmDto;
import co.dalicious.client.alarm.dto.PushRequestDto;
import co.dalicious.client.alarm.dto.PushRequestDtoByUser;
import co.dalicious.client.alarm.entity.PushAlarms;
import co.dalicious.client.alarm.entity.enums.AlarmType;
import co.dalicious.client.alarm.mapper.PushAlarmMapper;
import co.dalicious.client.alarm.repository.QPushAlarmsRepository;
import co.dalicious.data.redis.entity.PushAlarmHash;
import co.dalicious.data.redis.repository.PushAlarmHashRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.PushCondition;
import co.dalicious.domain.user.repository.*;
import co.dalicious.system.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
public class PushUtil {
    private final QUserSpotRepository qUserSpotRepository;
    private final QUserRepository qUserRepository;
    private final QPushAlarmsRepository qPushAlarmsRepository;
    private final PushAlarmMapper pushAlarmMapper;
    private final PushAlarmHashRepository pushAlarmHashRepository;

    @Transactional(readOnly = true)
    public PushRequestDto sendToType(Map<String, Set<BigInteger>> ids, PushCondition pushCondition, BigInteger contentId, String key, String customMessage) {
        Set<BigInteger> spotIds = !ids.containsKey("spotIds") || ids.get("spotIds") == null ? null : ids.get("spotIds");
        Set<BigInteger> userIds = !ids.containsKey("userIds") || ids.get("userIds") == null ? null : ids.get("userIds");

        // 활성화 된 자동 알람을 불러오기
        PushAlarms pushAlarms = qPushAlarmsRepository.findByPushCondition(pushCondition);
        // 비활성인 경우 알람 안보냄
        if (pushAlarms == null) return null;

        List<User> userList = new ArrayList<>();

        if (spotIds != null && !spotIds.isEmpty()) {
            //해당 스팟 유저의 fcm token 찾기
            userList = qUserSpotRepository.findAllUserSpotFirebaseToken(spotIds);
        }
        if (userIds != null && !userIds.isEmpty()) {
            userList = qUserRepository.findUserFirebaseToken(userIds);
        }

        List<String> firebaseTokenList = new ArrayList<>();
        List<BigInteger> pushUserIds = new ArrayList<>();
        userList.forEach(user -> {
            List<PushCondition> pushConditionList = user.getPushConditionList();
            if (pushConditionList == null || pushConditionList.isEmpty()) {
                return;
            }
            if (pushConditionList.contains(pushCondition)) {
                firebaseTokenList.add(user.getFirebaseToken());
                pushUserIds.add(user.getId());
            }
        });

        Map<String, String> keys = new HashMap<>();
        if (contentId != null) {
            keys.put(key, String.valueOf(contentId));
        }

        String message = pushAlarms.getMessage();
        if (PushCondition.getCustomMessageCondition().contains(pushCondition)) {
            message = customMessage;
        }

        return pushAlarmMapper.toPushRequestDto(firebaseTokenList, pushCondition.getTitle(), message, pushAlarms.getRedirectUrl(), keys);
    }

    @Transactional(readOnly = true)
    public PushRequestDtoByUser getPushRequest(User user, PushCondition pushCondition, String customMessage) {
        // 활성화 된 자동 알람을 불러오기
        PushAlarms pushAlarms = qPushAlarmsRepository.findByPushCondition(pushCondition);
        // 비활성인 경우 알람 안보냄
        if (pushAlarms == null) return null;

        String token = null;
        PushRequestDtoByUser pushRequestDto = null;

        List<PushCondition> pushConditionList = user.getPushConditionList();
        if (pushConditionList == null || pushConditionList.isEmpty()) {
            return null;
        }
        if (pushConditionList.contains(pushCondition) || PushCondition.getNoShowCondition().contains(pushCondition)) {
            token = user.getFirebaseToken();
        }

        String message = pushAlarms.getMessage();
        if (PushCondition.getCustomMessageCondition().contains(pushCondition)) {
            message = customMessage;
        }
        if (token != null) {
            pushRequestDto = pushAlarmMapper.toPushRequestDtoByUser(token, pushCondition.getTitle(), message, pushAlarms.getRedirectUrl());
        }

        return pushRequestDto;
    }

    public BatchAlarmDto getBatchAlarmDto(PushRequestDtoByUser pushRequestDtoByUser, User user) {
        return pushAlarmMapper.toBatchAlarmDto(pushRequestDtoByUser, user);
    }

    public static String getContextNewDailyFood(String template, String spotName, LocalDate startDate, LocalDate endDate) {
        // 식단이 생성 됐을 때 푸시알림
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("spotName", spotName);
        valuesMap.put("startDate", DateUtils.format(startDate));
        valuesMap.put("endDate", DateUtils.format(endDate));

        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        template = sub.replace(template);
        return template;
    }

    public static String getContextDeliveredOrderItem(String template, String userName, String foodName, String spotName) {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("user", userName);
        valuesMap.put("food", foodName);
        valuesMap.put("spot", spotName);

        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        template = sub.replace(template);
        return template;
    }

    @Transactional(readOnly = true)
    public String getContextOpenOrMySpot(String userName, String spotType, PushCondition pushCondition) {
        PushAlarms pushAlarms = qPushAlarmsRepository.findByPushCondition(pushCondition);
        String template = pushAlarms.getMessage();
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("user", userName);
        valuesMap.put("spotType", spotType);

        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        template = sub.replace(template);
        return template;
    }

    @Transactional(readOnly = true)
    public String getContextCorporationSpot(String userName, PushCondition pushCondition) {
        PushAlarms pushAlarms = qPushAlarmsRepository.findByPushCondition(pushCondition);
        String template = pushAlarms.getMessage();
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("user", userName);

        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        template = sub.replace(template);
        return template;
    }

    @Transactional
    public PushAlarmHash createPushAlarmHash (String title, String message, BigInteger userId, AlarmType alarmType, BigInteger reviewId) {
        return PushAlarmHash.builder()
                .title(title)
                .message(message)
                .isRead(false)
                .userId(userId)
                .type(alarmType.getAlarmType())
                .reviewId(reviewId)
                .build();
    }

    @Transactional
    public void savePushAlarmHash (String title, String message, BigInteger userId, AlarmType alarmType, BigInteger reviewId) {
        PushAlarmHash pushAlarmHash = PushAlarmHash.builder()
                .title(title)
                .message(message)
                .isRead(false)
                .userId(userId)
                .type(alarmType.getAlarmType())
                .reviewId(reviewId)
                .build();
        pushAlarmHashRepository.save(pushAlarmHash);
    }

    public List<List<PushRequestDtoByUser>> sliceByChunkSize(List<PushRequestDtoByUser> pushRequestDtoByUsers) {
        int chuckSize = 500;

        int originalListSize = pushRequestDtoByUsers.size();
        int numOfChunks = (int) Math.ceil((double) originalListSize / chuckSize);

        List<List<PushRequestDtoByUser>> chunkSizeList = new ArrayList<>(numOfChunks);

        for (int i = 0; i < numOfChunks; i++) {
            int startIndex = i * chuckSize;
            int endIndex = Math.min(startIndex + chuckSize, originalListSize);

            List<PushRequestDtoByUser> sublist = pushRequestDtoByUsers.subList(startIndex, endIndex);
            chunkSizeList.add(sublist);
        }

        return chunkSizeList;
    }
}
