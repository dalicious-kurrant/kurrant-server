package co.dalicious.domain.makers.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ServiceForm {
    RESTAURANT("홀매장", 1),
    DELIVERY_HALL("배달형매장(일반)", 2),
    DELIVERY_SHARED_KITCHEN("배달형매장(공용주방)", 3),
    MIXED_RESTAURANT("혼합형매장(공용주방)", 4);

    private final String serviceForm;
    private final Integer code;

    ServiceForm(String serviceForm, Integer code) {
        this.serviceForm = serviceForm;
        this.code = code;
    }

    public static ServiceForm ofCode(Integer dbData) {
        return Arrays.stream(ServiceForm.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }
}
