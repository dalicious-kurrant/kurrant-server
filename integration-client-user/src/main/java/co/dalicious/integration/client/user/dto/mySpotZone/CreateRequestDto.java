package co.dalicious.integration.client.user.dto.mySpotZone;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateRequestDto {

    private String name;
    private List<String> zipcodes;
    private String city;
    private List<String> counties;
    private List<String> villages;
    private Integer status;
    private String openStartDate;
    private String openCloseDate;
    private Integer userCount;
    private List<Integer> diningTypes;
    private List<String> breakfastDeliveryTime;
    private List<String> lunchDeliveryTime;
    private List<String> dinnerDeliveryTime;
    private String memo;
}
