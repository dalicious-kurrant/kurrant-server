package co.dalicious.system.enums;

import lombok.Getter;

import java.time.DayOfWeek;
import java.util.Arrays;

@Getter
public enum Days {
    MON("월", 0),
    TUE("화", 1),
    WED("수", 2),
    THR("목", 3),
    FRI("금", 4),
    SAT("토", 5),
    SUN("일", 6);

    private final String days;
    private final Integer code;

    Days(String days, Integer code) {
        this.days = days;
        this.code = code;
    }

    public static Days ofCode(Integer dbData) {
        return Arrays.stream(Days.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요일입니다."));
    }

    public static Days ofString(String dbData) {
        return Arrays.stream(Days.values())
                .filter(v -> v.getDays().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요일입니다."));
    }

    public static Days toDaysEnum(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> Days.MON;
            case TUESDAY -> Days.TUE;
            case WEDNESDAY -> Days.WED;
            case THURSDAY -> Days.THR;
            case FRIDAY -> Days.FRI;
            case SATURDAY -> Days.SAT;
            case SUNDAY -> Days.SUN;
            default -> throw new IllegalArgumentException("Unknown DayOfWeek: " + dayOfWeek);
        };
    }

    public static DayOfWeek toDayOfWeek(Days days) {
        return switch (days) {
            case MON -> DayOfWeek.MONDAY;
            case TUE -> DayOfWeek.TUESDAY;
            case WED -> DayOfWeek.WEDNESDAY;
            case THR -> DayOfWeek.THURSDAY;
            case FRI -> DayOfWeek.FRIDAY;
            case SAT -> DayOfWeek.SATURDAY;
            case SUN -> DayOfWeek.SUNDAY;
            default -> throw new IllegalArgumentException("Unknown DayOfWeek: " + days.getDays());
        };
    }
}
