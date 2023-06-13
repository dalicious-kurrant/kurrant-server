package co.dalicious.integration.client.user.dto.mySpotZone;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class CreateRequestDto {

    @NotNull(message = "이름은 필수입니다.")
    private String name;
    @NotNull(message = "우변번호는 필수입니다.")
    private List<String> zipcodes;
    @NotNull(message = "시/도는 필수입니다.")
    private String city;
    @NotNull(message = "시/군/구는 필수입니다.")
    private List<String> counties;
    @NotNull(message = "동/읍/리는 필수입니다.")
    private List<String> villages;
    private Integer status;
    private String openDate;
    private String closeDate;
    private Integer userCount;
    @NotNull(message = "식사타입은 필수입니다.")
    private List<Integer> diningTypes;
    @NotNull(message = "아침 배달 시간은 필수입니다.")
    private List<String> breakfastDeliveryTime;
    @NotNull(message = "점심 배달 시간은 필수입니다.")
    private List<String> lunchDeliveryTime;
    @NotNull(message = "저녁 배달 시간은 필수입니다.")
    private List<String> dinnerDeliveryTime;
    private String memo;
}
