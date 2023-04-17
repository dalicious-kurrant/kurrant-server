package co.dalicious.domain.user.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum JobType {
    MANAGEMENT("1001","경영", 1),
    AFFAIRS("1001","사무", 2),
    FINANCE("1001","금융", 3),
    INSURANCE("1001","보험", 4),
    RESEARCH("1002","연구직", 5),
    ENGINEERING("1002","공학기술", 6),
    IT("1002","정보기술", 7),
    EDUCATION("1003","교육", 8),
    LAW("1003","법률", 9),
    SOCIAL_WELFARE("1003","사회복지", 10),
    POLICE("1003","경찰", 11),
    FIRE_FIGHTER("1003","소방", 12),
    SOLDIER("1003","군인", 13),
    HEALTH("1004","보건", 14),
    MEDICAL("1004","의료", 15),
    ART("1005","예술", 16),
    DESIGN("1005","디자인", 17),
    BROADCASTING("1005","방송", 18),
    SPORTS("1005","스포츠", 19),
    BEAUTY("1006","미용", 20),
    TRAVEL("1006","여행", 21),
    ACCOMMODATION("1006","숙박", 22),
    FOOD("1006","음식", 23),
    SECURITY("1006","경비", 24),
    CLEANING("1006","청소", 25),
    SALES("1007","영업", 26),
    RETAIL("1007","판매", 27),
    DRIVER("1007","운전", 28),
    TRANSPORTATION("1007","운송", 29),
    CONSTRUCTION("1008","건설", 30),
    MINING("1008","채굴", 31),
    INSTALLATION("1009","설치", 32),
    MAINTENANCE("1009","정비", 33),
    PRODUCTION("1009","생산", 34),
    AGRICULTURE("1010","농업", 35),
    FORESTRY("1010","임업", 36),
    FISHERY("1010","어업", 37),
    LIVESTOCK("1010","축산업", 38),

    MANAGEMENT_AFFAIRS_FINANCE_INSURANCE("묶음","경영·사무·금융·보험직", 1001),
    RESEARCH_ENGINEERING("묶음","연구직 및 공학기술직", 1002),
    EDUCATION_LAW_SOCIAL_WELFARE_POLICE_FIRE_FIGHTER_SOLDIER("묶음","교육·법률·사회복지·경찰·소방직 및 군인", 1003),
    HEALTH_MEDICAL("묶음","보건·의료직", 1004),
    ART_DESIGN_BROADCASTING_SPORTS("묶음","예술·디자인·방송·스포츠직", 1005),
    BEAUTY_TRAVEL_ACCOMMODATION_FOOD_SECURITY_CLEANING("묶음","미용·여행·숙박·음식·경비·청소직", 1006),
    SALES_RETAIL_DRIVER_TRANSPORTATION("묶음","영업·판매·운전·운송직", 1007),
    CONSTRUCTION_MINING("묶음","건설·채굴직", 1008),
    INSTALLATION_MAINTENANCE_PRODUCTION("묶음","설치·정비·생산직", 1009),
    AGRICULTURE_FORESTRY_FISHERY_LIVESTOCK("묶음","농업·임업·어업·축산업직", 1010);

    private String category;
    private String name;
    private Integer code;

    JobType(String category, String name, Integer code) {
        this.category = category;
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
