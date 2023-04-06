package co.dalicious.domain.user.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum PointStatus {
    REVIEW_REWARD("리뷰 적립", 0),
    EVENT_REWARD("이벤트 적립", 1),
    CANCEL("환불", 2),
    USED("사용", 3),
    ADMIN_REWARD("운영자 적립", 4);

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

        return pointStatusList;
    }
}
