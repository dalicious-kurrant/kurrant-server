package co.dalicious.domain.order.util;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.FoodSchedule;
import co.dalicious.domain.food.repository.QFoodScheduleRepository;
import co.dalicious.domain.food.repository.QMakersScheduleRepository;
import co.dalicious.domain.food.entity.MakersSchedule;
import co.dalicious.domain.order.dto.FoodCountDto;
import co.dalicious.domain.order.dto.ServiceDateBy;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import exception.ApiException;
import exception.CustomException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class OrderDailyFoodUtil {
    private final QFoodScheduleRepository qFoodScheduleRepository;
    private final QMakersScheduleRepository qMakersScheduleRepository;
    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;


    public ServiceDateBy.MakersAndFood getMakersCapacity(List<DailyFood> dailyFoods, ServiceDateBy.MakersAndFood makersOrderCount) {
        // 2. 메이커스에서 값을 설정했는지 가져온다.
        List<MakersSchedule> makersSchedules = qMakersScheduleRepository.findAllByDailyFoods(dailyFoods);
        Map<ServiceDateBy.Makers, Integer> makersIntegerMap = new LinkedHashMap<>();
        ServiceDateBy.MakersAndFood makersAndFood = new ServiceDateBy.MakersAndFood();
        for (DailyFood dailyFood : dailyFoods) {
            Integer makersCount = 0;

            Optional<MakersSchedule> makersScheduleOptional = makersSchedules.stream()
                    .filter(v -> v.getServiceDate().equals(dailyFood.getServiceDate())
                            && v.getDiningType().equals(dailyFood.getDiningType()))
                    .findAny();

            if (makersScheduleOptional.isPresent()) {
                makersCount = makersScheduleOptional.get().getCapacity();
            } else {
                makersCount = dailyFood.getFood().getMakers().getMakersCapacities().stream()
                        .filter(v -> v.getDiningType().equals(dailyFood.getDiningType()))
                        .findAny()
                        .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "CE400015", dailyFood.getFood().getMakers().getId() + "번 " + dailyFood.getFood().getMakers().getName() + " 메이커스의 주문 가능 수량에 문제가 있습니다."))
                        .getCapacity();
            }
            Integer makersBuyCount = makersOrderCount.getMakersCount(dailyFood);
            ServiceDateBy.Makers makers = new ServiceDateBy.Makers();
            makers.setServiceDate(dailyFood.getServiceDate());
            makers.setDiningType(dailyFood.getDiningType());
            makers.setMakers(dailyFood.getFood().getMakers());
            makersIntegerMap.put(makers, makersCount - makersBuyCount);
        }
        makersAndFood.setMakersCountMap(makersIntegerMap);
        makersAndFood.setFoodCountMap(makersOrderCount.getFoodCountMap());
        return makersAndFood;
    }

    public Map<DailyFood, Integer> getRemainFoodsCount(List<DailyFood> dailyFoods) {
        // 존재하는 식단이 없을 경우 빈 Map을 리턴한다.
        if (dailyFoods.isEmpty()) return new HashMap<>();

        Map<DailyFood, Integer> dailyFoodIntegerMap = new HashMap<>();
        // 1. 한정 판매 수량인지 확인한다.
        List<FoodSchedule> foodSchedules = qFoodScheduleRepository.findAllByDailyFoods(dailyFoods);
        // 2. 메이커스에서 값을 설정했는지 가져온다.
        List<MakersSchedule> makersSchedules = qMakersScheduleRepository.findAllByDailyFoods(dailyFoods);
        // 3. 메이커스별/음식별 주문 수량을 가져온다
        ServiceDateBy.MakersAndFood makersAndFood = qOrderDailyFoodRepository.getMakersCounts(dailyFoods);
        // 4. 총 주문 수량을 가져온다.
        for (DailyFood dailyFood : dailyFoods) {
            Integer foodCount = 0;
            Integer makersCount = 0;

            // Find FoodSchedule and MakersSchedule for this DailyFood
            List<FoodSchedule> matchingFoodSchedules = foodSchedules.stream()
                    .filter(v -> v.getFood().equals(dailyFood.getFood())
                            && v.getServiceDate().equals(dailyFood.getServiceDate())
                            && v.getDiningType().equals(dailyFood.getDiningType()))
                    .toList();

            List<MakersSchedule> matchingMakersSchedules = makersSchedules.stream()
                    .filter(v -> v.getServiceDate().equals(dailyFood.getServiceDate())
                            && v.getDiningType().equals(dailyFood.getDiningType()))
                    .toList();

            // Calculate foodCount and makersCount
            if (!matchingFoodSchedules.isEmpty()) {
                foodCount = matchingFoodSchedules.get(0).getCapacity();
            } else {
                foodCount = dailyFood.getFood().getFoodCapacities().stream()
                        .filter(v -> v.getDiningType().equals(dailyFood.getDiningType()))
                        .findAny()
                        .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND))
                        .getCapacity();
            }

            if (!matchingMakersSchedules.isEmpty()) {
                makersCount = matchingMakersSchedules.get(0).getCapacity();
            } else {
                makersCount = dailyFood.getFood().getMakers().getMakersCapacities().stream()
                        .filter(v -> v.getDiningType().equals(dailyFood.getDiningType()))
                        .findAny()
                        .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND))
                        .getCapacity();
            }

            // Calculate sellableCount
            Integer makersBuyCount = makersAndFood.getMakersCount(dailyFood);
            Integer foodBuyCount = makersAndFood.getFoodCount(dailyFood);
            Integer sellableCount = Math.min(makersCount - makersBuyCount, foodCount - foodBuyCount);
            dailyFoodIntegerMap.put(dailyFood, sellableCount);
        }
        return dailyFoodIntegerMap;
    }

    public void getRemainMakersCount(List<DailyFood> dailyFoods) {
        // 메이커스에서 값을 설정했는지 가져온다.
        List<MakersSchedule> makersSchedules = qMakersScheduleRepository.findAllByDailyFoods(dailyFoods);
        // 3. 총 주문 수량을 가져온다.
        for (DailyFood dailyFood : dailyFoods) {
            Integer makersCount = 0;

            Optional<MakersSchedule> makersScheduleOptional = makersSchedules.stream()
                    .filter(v -> v.getServiceDate().equals(dailyFood.getServiceDate())
                            && v.getDiningType().equals(dailyFood.getDiningType()))
                    .findAny();

            if (makersScheduleOptional.isPresent()) {
                makersCount = makersScheduleOptional.get().getCapacity();
            } else {
                makersCount = dailyFood.getFood().getMakers().getMakersCapacity(dailyFood.getDiningType()).getCapacity();
            }
            Integer makersBuyCount = qOrderDailyFoodRepository.getMakersCount(dailyFood);
            Integer sellableCount = makersCount - makersBuyCount;
        }
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
