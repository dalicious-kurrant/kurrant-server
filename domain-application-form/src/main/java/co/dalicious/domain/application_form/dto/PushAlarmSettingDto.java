package co.dalicious.domain.application_form.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class PushAlarmSettingDto {
    private BigInteger id;
    private Integer spotType;
}
