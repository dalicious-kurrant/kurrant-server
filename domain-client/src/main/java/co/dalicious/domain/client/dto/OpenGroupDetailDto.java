package co.dalicious.domain.client.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class OpenGroupDetailDto {
    private BigInteger id;
    private String name;
    private String address;
    private String jibun;
    private List<Integer> diningTypes;
    private List<String> breakfastDeliveryTime;
    private List<String> lunchDeliveryTime;
    private List<String> dinnerDeliveryTime;
    private List<OpenGroupSpotDetailDto> spotDetailDtos;
    private Integer userCount;
}
