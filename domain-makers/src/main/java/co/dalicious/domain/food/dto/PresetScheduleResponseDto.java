package co.dalicious.domain.food.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class PresetScheduleResponseDto {

    private BigInteger presetMakersId;
    private String makersName;
    private Integer scheduleStatus;
    private String serviceDate;
    private String diningType;
    private Integer makersCapacity;
    private String deadline;
    private List<ClientSchedule> clientSchedule;


    @Getter
    @Setter
    public static class ClientSchedule {

        private List<String> pickupTime;
        private String clientName;
        private Integer clientCapacity;
        private List<FoodSchedule> foodSchedule;

    }

    @Getter
    @Setter
    public static class FoodSchedule {
        private BigInteger presetFoodId;
        private String foodName;
        private String foodStatus;
        private Integer foodCapacity;
        private Integer scheduleStatus;

        @Builder
        public FoodSchedule(BigInteger presetFoodId, String foodName, String foodStatus, Integer foodCapacity, Integer scheduleStatus) {
            this.presetFoodId = presetFoodId;
            this.foodName = foodName;
            this.foodStatus = foodStatus;
            this.foodCapacity = foodCapacity;
            this.scheduleStatus = scheduleStatus;
        }
    }

    @Builder
    public PresetScheduleResponseDto(BigInteger presetMakersId, Integer scheduleStatus, String serviceDate, String makersName,
                                     String diningType, Integer makersCapacity, String deadline, List<ClientSchedule> clientSchedule) {

        this.presetMakersId = presetMakersId;
        this.makersName = makersName;
        this.scheduleStatus = scheduleStatus;
        this.serviceDate = serviceDate;
        this.diningType = diningType;
        this.makersCapacity = makersCapacity;
        this.deadline = deadline;
        this.clientSchedule = clientSchedule;
    }
}


