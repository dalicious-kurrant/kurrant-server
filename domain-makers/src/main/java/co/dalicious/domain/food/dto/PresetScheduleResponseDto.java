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
    private List<clientSchedule> clientSchedule;


    @Getter
    @Setter
    public static class clientSchedule {

        private String pickupTime;
        private String clientName;
        private Integer clientCapacity;
        private Integer leftMakersCapacity;
        private List<foodSchedule> foodSchedule;

        @Builder
        public clientSchedule(String pickupTime, String clientName, Integer clientCapacity, List<foodSchedule> foodSchedule) {
            this.pickupTime = pickupTime;
            this.clientName = clientName;
            this.clientCapacity = clientCapacity;
            this.foodSchedule = foodSchedule;
        }

        @Builder
        public clientSchedule(String pickupTime, String clientName, Integer clientCapacity, List<foodSchedule> foodSchedule, Integer leftMakersCapacity) {
            this.pickupTime = pickupTime;
            this.clientName = clientName;
            this.clientCapacity = clientCapacity;
            this.foodSchedule = foodSchedule;
            this.leftMakersCapacity = leftMakersCapacity;
        }

    }

    @Getter
    @Setter
    public static class foodSchedule {
        private BigInteger presetFoodId;
        private String foodName;
        private String foodStatus;
        private Integer foodCapacity;
        private Integer scheduleStatus;
        private Integer leftFoodCapacity;

        @Builder
        public foodSchedule(BigInteger presetFoodId, String foodName, String foodStatus, Integer foodCapacity, Integer scheduleStatus) {
            this.presetFoodId = presetFoodId;
            this.foodName = foodName;
            this.foodStatus = foodStatus;
            this.foodCapacity = foodCapacity;
            this.scheduleStatus = scheduleStatus;
        }

        @Builder
        public foodSchedule(BigInteger presetFoodId, String foodName, String foodStatus, Integer foodCapacity, Integer scheduleStatus, Integer leftFoodCapacity) {
            this.presetFoodId = presetFoodId;
            this.foodName = foodName;
            this.foodStatus = foodStatus;
            this.foodCapacity = foodCapacity;
            this.scheduleStatus = scheduleStatus;
            this.leftFoodCapacity = leftFoodCapacity;
        }
    }

    @Builder
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

    @Builder
    public PresetScheduleResponseDto(BigInteger presetMakersId, Integer scheduleStatus, String serviceDate,
                                          String diningType, Integer makersCapacity, String deadline, List<clientSchedule> clientSchedule, String makersName) {

        this.presetMakersId = presetMakersId;
        this.scheduleStatus = scheduleStatus;
        this.serviceDate = serviceDate;
        this.diningType = diningType;
        this.makersCapacity = makersCapacity;
        this.deadline = deadline;
        this.clientSchedule = clientSchedule;
        this.makersName = makersName;
    }
}


