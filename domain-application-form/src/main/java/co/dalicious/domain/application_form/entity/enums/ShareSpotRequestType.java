package co.dalicious.domain.application_form.entity.enums;

import exception.CustomException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
public enum ShareSpotRequestType {
    REGISTER("신청", 1),
    ADD("추가", 2),
    TIME_ADD("시간 추가", 3);
    private final String type;
    private final Integer code;

    ShareSpotRequestType(String type, Integer code) {
        this.type = type;
        this.code = code;
    }

    public static ShareSpotRequestType ofCode(Integer code) {
        return Arrays.stream(ShareSpotRequestType.values())
                .findAny()
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "CE4000013", "존재하지 않는 공유 스팟 신청"));
    }
}
