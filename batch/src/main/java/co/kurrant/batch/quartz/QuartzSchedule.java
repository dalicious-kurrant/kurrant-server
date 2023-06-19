package co.kurrant.batch.quartz;

import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.client.repository.QMealInfoRepository;
import co.dalicious.domain.food.repository.QFoodCapacityRepository;
import co.dalicious.domain.food.repository.QMakersCapacityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class QuartzSchedule {
    private final QMealInfoRepository qMealInfoRepository;
    private final QMakersCapacityRepository qMakersCapacityRepository;
    private final QFoodCapacityRepository qFoodCapacityRepository;
    public List<String> getGroupLastOrderTimeCron() {
        List<String> crons = new ArrayList<>();
        List<DayAndTime> groupLastOrderTimes = getGroupLastOrderTime();
        for (DayAndTime groupLastOrderTime : groupLastOrderTimes) {
            crons.add(String.format("0 %d %d * * ?", groupLastOrderTime.getTime().getMinute(), groupLastOrderTime.getTime().getHour()));
        }
        return crons;
    }

    public List<String> getMakersAndFoodLastOrderTimeCron() {
        List<String> crons = new ArrayList<>();
        List<DayAndTime> makersLastOrderTimes = getMakersLastOrderTime();
        List<DayAndTime> foodsLastOrderTimes = getFoodsLastOrderTime();
        Set<DayAndTime> dayAndTimes = new HashSet<>(makersLastOrderTimes);
        dayAndTimes.addAll(foodsLastOrderTimes);
        for (DayAndTime dayAndTime : dayAndTimes) {
            crons.add(String.format("0 %d %d * * ?", dayAndTime.getTime().getMinute(), dayAndTime.getTime().getHour()));
        }
        return crons;
    }
    private List<DayAndTime> getGroupLastOrderTime() {
        return qMealInfoRepository.getMealInfoLastOrderTime();
    }
    private List<DayAndTime> getMakersLastOrderTime() {
        return qMakersCapacityRepository.getMakersCapacityLastOrderTime();
    }
    private List<DayAndTime> getFoodsLastOrderTime() {
        return qFoodCapacityRepository.getFoodCapacityLastOrderTime();
    }
}
