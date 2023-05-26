package co.dalicious.domain.client.dto.mySpotZone;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class AdminListResponseDto {
    private BigInteger id;
    private String name;
    private String city;
    private Set<String> counties;
    private Set<String> villages;
    private List<String> zipcodes;
    private Integer status;
    private String openStartDate;
    private String openCloseDate;
    private List<Integer> diningType;
    private List<String> breakfastDeliveryTime;
    private List<String> lunchDeliveryTime;
    private List<String> dinnerDeliveryTime;
    private Integer userCount;
}
