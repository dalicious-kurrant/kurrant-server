package co.dalicious.domain.application_form.dto.mySpotZone;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class UpdateStatusDto {
    private List<BigInteger> ids;
    private Integer status;
    private LocalDate startDate;
}
