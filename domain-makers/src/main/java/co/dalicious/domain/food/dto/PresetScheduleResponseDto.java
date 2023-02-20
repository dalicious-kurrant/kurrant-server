package co.dalicious.domain.food.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Builder
public class PresetScheduleResponseDto {

    private BigInteger presetMakersId;
    private Integer scheduleStatus;
    private String serviceDate;
    private String diningType;
    private Integer makersCapacity;
    private String deadline;
    private List<clientSchedule> clientSchedule;


    @Getter
    @Setter
    @Builder
    public static class clientSchedule {

        private String pickupTime;
        private String clientName;
        private Integer clientCapacity;
        private List<foodSchedule> foodSchedule;

        public clientSchedule(String pickupTime, String clientName, Integer clientCapacity, List<foodSchedule> foodSchedule) {
            this.pickupTime = pickupTime;
            this.clientName = clientName;
            this.clientCapacity = clientCapacity;
            this.foodSchedule = foodSchedule;
        }
    }

    @Getter
    @Setter
    @Builder
    public static class foodSchedule {
        private BigInteger presetFoodId;
        private String foodName;
        private String foodStatus;
        private Integer foodCapacity;
        private Integer scheduleStatus;


        public foodSchedule(BigInteger presetFoodId, String foodName, String foodStatus, Integer foodCapacity, Integer scheduleStatus) {
            this.presetFoodId = presetFoodId;
            this.foodName = foodName;
            this.foodStatus = foodStatus;
            this.foodCapacity = foodCapacity;
            this.scheduleStatus = scheduleStatus;
        }
    }

    public PresetScheduleResponseDto(BigInteger presetMakersId, Integer scheduleStatus, String serviceDate,
                                     String diningType, Integer makersCapacity, String deadline, List<clientSchedule> clientSchedule) {

        this.presetMakersId = presetMakersId;
        this.scheduleStatus = scheduleStatus;
        this.serviceDate = serviceDate;
        this.diningType = diningType;
        this.makersCapacity = makersCapacity;
        this.deadline = deadline;
        this.clientSchedule = clientSchedule;
    }
}

