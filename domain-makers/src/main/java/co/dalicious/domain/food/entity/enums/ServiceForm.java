package co.dalicious.domain.food.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum ServiceForm {
    DELIVERY_ONLY("배달전용매장", 1),
    EAT_IN_ONLY("홀전용매장", 2),
    EAT_IN_AND_DELIVERY("혼합매장", 3),
    ;

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

    public  static ServiceForm ofString(String dbData){
        return Arrays.stream(ServiceForm.values())
                .filter(v -> v.getServiceForm().equals(dbData))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }

    public static List<ServiceForm> getContainEatIn() {
        List<ServiceForm> serviceForms = new ArrayList<>();
        serviceForms.add(EAT_IN_ONLY);
        serviceForms.add(EAT_IN_AND_DELIVERY);
        return serviceForms;
    }

}
