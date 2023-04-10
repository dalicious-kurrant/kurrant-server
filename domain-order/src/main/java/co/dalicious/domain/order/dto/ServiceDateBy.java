package co.dalicious.domain.order.dto;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.system.enums.DiningType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

public class ServiceDateBy {

    @Getter
    @Setter
    public static class MakersAndFood {
        private Map<Makers, Integer> makersCountMap;
        private Map<Food, Integer> foodCountMap;

        public Integer getMakersCount(DailyFood dailyFood) {
            Makers makers = new Makers(dailyFood);
            return (this.makersCountMap.get(makers) == null) ? 0 : this.makersCountMap.get(makers);
        }

        public Integer getFoodCount(DailyFood dailyFood) {
            Food food = new Food(dailyFood);
            return (this.foodCountMap.get(food) == null) ? 0 : this.foodCountMap.get(food);
        }
    }
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Makers {
        private LocalDate serviceDate;
        private DiningType diningType;
        private co.dalicious.domain.food.entity.Makers makers;

        public Makers(DailyFood dailyFood) {
            this.serviceDate = dailyFood.getServiceDate();
            this.diningType = dailyFood.getDiningType();
            this.makers = dailyFood.getFood().getMakers();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof ServiceDateBy.Makers tmp) {
                return serviceDate.equals(tmp.serviceDate) && diningType.equals(tmp.diningType) && makers.equals(tmp.makers);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(serviceDate, diningType, makers);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Food {
        private LocalDate serviceDate;
        private DiningType diningType;
        private co.dalicious.domain.food.entity.Food food;

        public Food(DailyFood dailyFood) {
            this.serviceDate = dailyFood.getServiceDate();
            this.diningType = dailyFood.getDiningType();
            this.food = dailyFood.getFood();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof ServiceDateBy.Food tmp) {
                return serviceDate.equals(tmp.serviceDate) && diningType.equals(tmp.diningType) && food.equals(tmp.food);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(serviceDate, diningType, food);
        }
    }
}
