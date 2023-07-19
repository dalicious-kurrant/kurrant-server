package co.dalicious.domain.delivery.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Getter
@NoArgsConstructor
public class DeliveryInstanceDto {
    private String id;
    private String deliveryDate;
    private String diningType;
    private String deliveryTime;
    private String groupName;
    private List<String> makersNames;
    private String driver;

    @Builder
    public DeliveryInstanceDto(String id, String deliveryDate, String diningType, String deliveryTime, String groupName, List<String> makersNames, String driver) {
        this.id = id;
        this.deliveryDate = deliveryDate;
        this.diningType = diningType;
        this.deliveryTime = deliveryTime;
        this.groupName = groupName;
        this.makersNames = makersNames;
        this.driver = driver;
    }

    public Boolean isTempDto() {
        return this.id.startsWith("temp");
    }

    public BigInteger getDatabaseId() {
        if(!isTempDto()) {
            String[] ids = this.getId().split("_");
            return BigInteger.valueOf(Integer.parseInt(ids[0]));
        }
        return null;
    }
}