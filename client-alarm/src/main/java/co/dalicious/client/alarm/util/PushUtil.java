package co.dalicious.client.alarm.util;

import co.dalicious.client.alarm.dto.PushRequestDto;
import co.dalicious.client.alarm.dto.PushRequestDtoByUser;
import co.dalicious.client.alarm.entity.PushAlarms;
import co.dalicious.client.alarm.mapper.PushAlarmMapper;
import co.dalicious.client.alarm.repository.QPushAlarmsRepository;
import co.dalicious.client.alarm.service.PushService;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.PushCondition;
import co.dalicious.domain.user.repository.QUserGroupRepository;
import co.dalicious.domain.user.repository.QUserRepository;
import co.dalicious.domain.user.repository.QUserSpotRepository;
import co.dalicious.system.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
public class PushUtil {

    private final QUserGroupRepository qUserGroupRepository;
    private final QUserSpotRepository qUserSpotRepository;
    private final QUserRepository qUserRepository;
    private final QPushAlarmsRepository qPushAlarmsRepository;
    private final PushService pushService;
    private final PushAlarmMapper pushAlarmMapper;

    public void sendToType(Map<String, Set<BigInteger>> ids, PushCondition pushCondition, BigInteger contentId, String key, String customMessage) {
        Set<BigInteger> groupIds = !ids.containsKey("groupIds") || ids.get("groupIds") == null ? null : ids.get("groupIds");
        Set<BigInteger> spotIds = !ids.containsKey("spotIds") || ids.get("spotIds") == null ? null : ids.get("spotIds");
        Set<BigInteger> userIds = !ids.containsKey("userIds") || ids.get("userIds") == null ? null : ids.get("userIds");

        // 활성화 된 자동 알람을 불러오기
        PushAlarms pushAlarms = qPushAlarmsRepository.findByPushCondition(pushCondition);
        // 비활성인 경우 알람 안보냄
        if (pushAlarms == null) return;

        List<User> userList = new ArrayList<>();

        if (groupIds != null && !groupIds.isEmpty()) {
            //해당 그룹 유저의 fcm token 찾기
            userList = qUserGroupRepository.findUserGroupFirebaseToken(groupIds);
        }
        if (spotIds != null && !spotIds.isEmpty()) {
            //해당 스팟 유저의 fcm token 찾기
            userList = qUserSpotRepository.findAllUserSpotFirebaseToken(spotIds);
        }
        if (userIds != null && !userIds.isEmpty()) {
            userList = qUserRepository.findUserFirebaseToken(userIds);
        }

        List<String> firebaseTokenList = new ArrayList<>();

        userList.forEach(user -> {
            List<PushCondition> pushConditionList = user.getPushConditionList();
            if (pushConditionList == null || pushConditionList.isEmpty()) {
                return;
            }
            if (pushConditionList.contains(pushCondition)) {
                firebaseTokenList.add(user.getFirebaseToken());
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

        PushRequestDto pushRequestDto = pushAlarmMapper.toPushRequestDto(firebaseTokenList, pushCondition.getTitle(), message, pushAlarms.getRedirectUrl(), keys);

        pushService.sendToPush(pushRequestDto);
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

    public static String getContextNewDailyFood(String template, LocalDate startDate, LocalDate endDate) {
        // 식단이 생성 됐을 때 푸시알림
        Map<String, String> valuesMap = new HashMap<>();
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
}
