package co.dalicious.domain.order.util;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.FoodSchedule;
import co.dalicious.domain.food.repository.QFoodScheduleRepository;
import co.dalicious.domain.food.repository.QMakersScheduleRepository;
import co.dalicious.domain.food.entity.MakersSchedule;
import co.dalicious.domain.order.dto.FoodCountDto;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderDailyFoodUtil {
    private final QFoodScheduleRepository qFoodScheduleRepository;
    private final QMakersScheduleRepository qMakersScheduleRepository;
    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;

    public Map<DailyFood, Integer> getRemainFoodsCount(List<DailyFood> dailyFoods) {
        Map<DailyFood, Integer> dailyFoodIntegerMap = new HashMap<>();
        // 1. 한정 판매 수량인지 확인한다.
        List<FoodSchedule> foodSchedules = qFoodScheduleRepository.findAllByDailyFoods(dailyFoods);
        // 2. 메이커스에서 값을 설정했는지 가져온다.
        List<MakersSchedule> makersSchedules = qMakersScheduleRepository.findAllByDailyFoods(dailyFoods);
        // 3. 총 주문 수량을 가져온다.
        for (DailyFood dailyFood : dailyFoods) {
            Integer foodCount = 0;
            Integer makersCount = 0;

            Optional<FoodSchedule> foodScheduleOptional = foodSchedules.stream().filter(v -> v.getFood().equals(dailyFood.getFood())
                            && v.getServiceDate().equals(dailyFood.getServiceDate())
                            && v.getDiningType().equals(dailyFood.getDiningType()))
                    .findAny();
            Optional<MakersSchedule> makersScheduleOptional = makersSchedules.stream()
                    .filter(v -> v.getServiceDate().equals(dailyFood.getServiceDate())
                            && v.getDiningType().equals(dailyFood.getDiningType()))
                    .findAny();

            if (foodScheduleOptional.isPresent()) {
                foodCount = foodScheduleOptional.get().getCapacity();
            } else {
                foodCount = dailyFood.getFood().getFoodCapacities().stream()
                        .filter(v -> v.getDiningType().equals(dailyFood.getDiningType()))
                        .findAny()
                        .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND))
                        .getCapacity();
            }

            if (makersScheduleOptional.isPresent()) {
                makersCount = makersScheduleOptional.get().getCapacity();
            } else {
                makersCount = dailyFood.getFood().getMakers().getMakersCapacities().stream()
                        .filter(v -> v.getDiningType().equals(dailyFood.getDiningType()))
                        .findAny()
                        .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND))
                        .getCapacity();
            }
            Integer makersBuyCount = qOrderDailyFoodRepository.getMakersCount(dailyFood);
            Integer foodBuyCount = qOrderDailyFoodRepository.getFoodCount(dailyFood);
            Integer sellableCount = Math.min(makersCount - makersBuyCount, foodCount - foodBuyCount);
            dailyFoodIntegerMap.put(dailyFood, sellableCount);
        }
        return dailyFoodIntegerMap;
    }

    public FoodCountDto getRemainFoodCount(DailyFood dailyFood) {
        // 1. 한정 판매 수량인지 확인한다.
        FoodSchedule foodSchedule = qFoodScheduleRepository.findOneByDailyFood(dailyFood);
        // 2. 메이커스에서 값을 설정했는지 가져온다.
        MakersSchedule makersSchedule = qMakersScheduleRepository.findOneByDailyFood(dailyFood);
        // 3. 총 주문 수량을 가져온다.
        Integer foodCount = 0;
        Integer makersCount = 0;

        if (foodSchedule != null) {
            foodCount = foodSchedule.getCapacity();
        } else {
            foodCount = dailyFood.getFood().getFoodCapacities().stream()
                    .filter(v -> v.getDiningType().equals(dailyFood.getDiningType()))
                    .findAny()
                    .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND))
                    .getCapacity();
        }

        if (makersSchedule != null) {
            makersCount = makersSchedule.getCapacity();
        } else {
            makersCount = dailyFood.getFood().getMakers().getMakersCapacities().stream()
                    .filter(v -> v.getDiningType().equals(dailyFood.getDiningType()))
                    .findAny()
                    .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND))
                    .getCapacity();
        }
        Integer makersBuyCount = qOrderDailyFoodRepository.getMakersCount(dailyFood);
        Integer foodBuyCount = qOrderDailyFoodRepository.getFoodCount(dailyFood);
        Boolean isFollowingMakersCapacity = makersCount - makersBuyCount < foodCount - foodBuyCount;
        Integer sellableCount = Math.min(makersCount - makersBuyCount, foodCount - foodBuyCount);
        return new FoodCountDto(sellableCount, isFollowingMakersCapacity);
    }
}
