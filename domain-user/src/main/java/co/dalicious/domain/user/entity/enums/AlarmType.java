package co.dalicious.domain.user.entity.enums;

import lombok.Getter;

@Getter
public enum AlarmType {
    MARKETING_ALARM("혜택 및 소식 알림"),
    ORDER_ALARM("주문 알림"),
    ALL_ALARM("마케팅 정보 수신 동의");

    private final String alarmType;

    AlarmType(String alarmType) {
        this.alarmType = alarmType;
    }
}
