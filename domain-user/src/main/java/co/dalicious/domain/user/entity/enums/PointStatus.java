package co.dalicious.domain.user.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import javax.swing.plaf.ComponentInputMapUIResource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum PointStatus {
    REVIEW_REWARD("리뷰 적립", 0),
    EVENT_REWARD("이벤트 적립", 1),
    CANCEL("환불", 2),
    USED("사용", 3),
    ADMIN_REWARD("운영자 적립", 4),
    ADMIN_POINTS_RECOVERED("운영자 차감", 5),
    FOUNDERS_REWARD("파운더스 적립", 6),
    ACCUMULATED_FOUNDERS_POINT("파운더스 적립", 7);

    private final String type;
    private final Integer code;

    PointStatus(String type, Integer code) {
        this.type = type;
        this.code = code;
    }

    public static PointStatus ofCode(Integer dbData) {
        return Arrays.stream(PointStatus.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }

    public static List<PointStatus> rewardStatus() {
        List<PointStatus> pointStatusList = new ArrayList<>();
        pointStatusList.add(PointStatus.REVIEW_REWARD);
        pointStatusList.add(PointStatus.EVENT_REWARD);
        pointStatusList.add(PointStatus.CANCEL);
        pointStatusList.add(PointStatus.ADMIN_REWARD);
        pointStatusList.add(PointStatus.FOUNDERS_REWARD);
        pointStatusList.add(PointStatus.ACCUMULATED_FOUNDERS_POINT);

        return pointStatusList;
    }

    public static List<PointStatus> userStatus() {
        List<PointStatus> pointStatusList = new ArrayList<>();
        pointStatusList.add(PointStatus.USED);
        pointStatusList.add(PointStatus.ADMIN_POINTS_RECOVERED);

        return pointStatusList;
    }
}
