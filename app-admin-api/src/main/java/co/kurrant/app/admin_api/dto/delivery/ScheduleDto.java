package co.kurrant.app.admin_api.dto.delivery;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
public class ScheduleDto {
    private String deliveryDate;
    private String diningType;
    private String deliveryTime;
    private String groupName;
    private List<String> makersNames;
    private String driver;

    @Builder
    public ScheduleDto(String deliveryDate, String diningType, String deliveryTime, String groupName, List<String> makersNames, String driver) {
        this.deliveryDate = deliveryDate;
        this.diningType = diningType;
        this.deliveryTime = deliveryTime;
        this.groupName = groupName;
        this.makersNames = makersNames;
        this.driver = driver;
    }
}
