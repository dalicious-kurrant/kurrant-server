package co.dalicious.integration.client.user.dto.mySpotZone;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class UpdateRequestDto {
    private BigInteger id;
    private String name;
    private List<String> zipcodes;
    private String city;
    private List<String> counties;
    private List<String> villages;
    private Integer status;
    private String openDate;
    private String closeDate;
    private List<Integer> diningTypes;
    private List<String> breakfastDeliveryTime;
    private List<String> lunchDeliveryTime;
    private List<String> dinnerDeliveryTime;
    private String memo;
}
