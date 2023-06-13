package co.dalicious.client.alarm.util;

import co.dalicious.client.alarm.dto.BatchAlarmDto;
import co.dalicious.client.alarm.dto.PushRequestDto;
import co.dalicious.client.alarm.dto.PushRequestDtoByUser;
import co.dalicious.client.alarm.entity.PushAlarms;
import co.dalicious.client.alarm.mapper.PushAlarmMapper;
import co.dalicious.client.alarm.repository.QPushAlarmsRepository;
import co.dalicious.client.alarm.service.PushService;
import co.dalicious.domain.user.entity.BatchPushAlarmLog;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
@RequiredArgsConstructor
public class PushUtil {
    private final QUserSpotRepository qUserSpotRepository;
    private final QUserRepository qUserRepository;
    private final QPushAlarmsRepository qPushAlarmsRepository;
    private final PushService pushService;
    private final PushAlarmMapper pushAlarmMapper;

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
        if (pushConditionList.contains(pushCondition)) {
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

    public void getBatchAlarmDto(User user, PushCondition pushCondition) {
        // 활성화 된 자동 알람을 불러오기
        PushAlarms pushAlarms = qPushAlarmsRepository.findByPushCondition(pushCondition);
        // 비활성인 경우 알람 안보냄
        if (pushAlarms == null) return;

        Map<String, BigInteger> token = new HashMap<>();
        BatchAlarmDto pushRequestDto = null;

        List<PushCondition> pushConditionList = user.getPushConditionList();
        if (pushConditionList == null || pushConditionList.isEmpty()) {
            return;
        }
        if (pushConditionList.contains(pushCondition)) {
            token.put(user.getFirebaseToken(), user.getId());
        }

        String message = pushAlarms.getMessage();
        if (!token.isEmpty()) {
            pushRequestDto = pushAlarmMapper.toBatchAlarmDto(token, pushCondition.getTitle(), pushAlarms.getRedirectUrl(), message);
        }

        if(pushRequestDto != null) {
            pushService.sendToPush(pushRequestDto, pushCondition);
        }
    }

    public static String getContextOpenOrMySpot(String template, String userName, String spotType) {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("user", userName);
        valuesMap.put("spotType", spotType);

        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        template = sub.replace(template);
        return template;
    }
}
