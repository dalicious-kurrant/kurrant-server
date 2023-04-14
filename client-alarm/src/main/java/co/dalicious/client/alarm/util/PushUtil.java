package co.dalicious.client.alarm.util;

import co.dalicious.client.alarm.dto.PushRequestDto;
import co.dalicious.client.alarm.entity.PushAlarms;
import co.dalicious.client.alarm.mapper.PushAlarmMapper;
import co.dalicious.client.alarm.repository.QPushAlarmsRepository;
import co.dalicious.client.alarm.service.PushService;
import co.dalicious.domain.user.entity.enums.PushCondition;
import co.dalicious.domain.user.repository.QUserGroupRepository;
import co.dalicious.domain.user.repository.QUserRepository;
import co.dalicious.domain.user.repository.QUserSpotRepository;
import co.dalicious.system.util.DateUtils;
import lombok.RequiredArgsConstructor;
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

    public void sendToType(Set<BigInteger> groupIds, Set<BigInteger> spotIds, Set<BigInteger> userIds,
                           PushCondition pushCondition, BigInteger contentId, String key, LocalDate date) {
        // 활성화 된 자동 알람을 불러오기
        PushAlarms pushAlarms = qPushAlarmsRepository.findByPushCondition(pushCondition);
        // 비활성인 경우 알람 안보냄
        if(pushAlarms == null) return;

        List<String> firebaseToken = new ArrayList<>();

        if(groupIds != null && !groupIds.isEmpty()) {
            //해당 그룹 유저의 fcm token 찾기
            firebaseToken = qUserGroupRepository.findUserGroupFirebaseToken(groupIds, pushCondition);
        }
        if(spotIds != null && !spotIds.isEmpty()) {
            //해당 스팟 유저의 fcm token 찾기
            firebaseToken = qUserSpotRepository.findAllUserSpotFirebaseToken(spotIds, pushCondition);
        }
        if(userIds != null && !userIds.isEmpty()) {
            firebaseToken = qUserRepository.findUserFirebaseToken(userIds, pushCondition);
        }

        Map<String, String> keys = new HashMap<>();
        if(contentId != null) {
            keys.put(key, String.valueOf(contentId));
        }
        if(date != null) {
            keys.put(key, DateUtils.format(date));
        }

        PushRequestDto pushRequestDto = pushAlarmMapper.toPushRequestDto(firebaseToken, null, pushAlarms.getMessage(), pushAlarms.getRedirectUrl(), keys);

        pushService.sendToPush(pushRequestDto);
    }
}
