package co.dalicious.domain.user.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum JobType {
    MANAGEMENT("경영", 1),
    AFFAIRS("사무", 2),
    FINANCE("금융", 3),
    INSURANCE("보험", 4),
    RESEARCH("연구직", 5),
    ENGINEERING("공학기술", 6),
    IT("정보기술", 7),
    EDUCATION("교육", 8),
    LAW("법률", 9),
    SOCIAL_WELFARE("사회복지", 10),
    POLICE("경찰", 11),
    FIRE_FIGHTER("소방", 12),
    SOLDIER("군인", 13),
    HEALTH("보건", 14),
    MEDICAL("의료", 15),
    ART("예술", 16),
    DESIGN("디자인", 17),
    BROADCASTING("방송", 18),
    SPORTS("스포츠", 19),
    BEAUTY("미용", 20),
    TRAVEL("여행", 21),
    ACCOMMODATION("숙박", 22),
    FOOD("음식", 23),
    SECURITY("경비", 24),
    CLEANING("청소", 25),
    SALES("영업", 26),
    RETAIL("판매", 27),
    DRIVER("운전", 28),
    TRANSPORTATION("운송", 29),
    CONSTRUCTION("건설", 30),
    MINING("채굴", 31),
    INSTALLATION("설치", 32),
    MAINTENANCE("정비", 33),
    PRODUCTION("생산", 34),
    AGRICULTURE("농업", 35),
    FORESTRY("임업", 36),
    FISHERY("어업", 37),
    LIVESTOCK("축산업", 38);

    private final String name;
    private final Integer code;

    JobType(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    public static JobType ofCode(Integer code){
        return Arrays.stream(JobType.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }

    public static JobType ofValue(String value){
        return Arrays.stream(JobType.values())
                .filter(v -> v.getName().equals(value))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }


}
