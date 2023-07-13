package co.kurrant.app.admin_api.dto.delivery;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class ScheduleDto {
    private String deliveryDate;

    @Getter
    @Setter
    public static class Driver {
        private String name;
        private List<BigInteger> spotIds;
    }
}
