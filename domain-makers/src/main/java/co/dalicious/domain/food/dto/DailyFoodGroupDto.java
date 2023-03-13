package co.dalicious.domain.food.dto;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
public class DailyFoodGroupDto {
    private LocalDate serviceDate;
    private DiningType diningType;
    private String makersName;
    private String groupName;

    public DailyFoodGroupDto(FoodDto.DailyFood dailyFood) {
        this.serviceDate = DateUtils.stringToDate(dailyFood.getServiceDate());
        this.diningType = DiningType.ofCode(dailyFood.getDiningType());
        this.makersName = dailyFood.getMakersName();
        this.groupName = dailyFood.getGroupName();
    }

    public boolean equals(Object obj) {
        if(obj instanceof DailyFoodGroupDto tmp) {
            return serviceDate.equals(tmp.serviceDate) && diningType.equals(tmp.diningType)
                    && makersName.equals(tmp.makersName) && groupName.equals(tmp.groupName);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(serviceDate, diningType, makersName, groupName);
    }
}
