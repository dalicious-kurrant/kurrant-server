package co.dalicious.domain.order.dto;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.system.enums.DiningType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class OrderCount {
    private Group group;
    private List<Count> counts;

    public Integer getTotalCount() {
        return counts.stream()
                .map(Count::getCount)
                .reduce(0, Integer::sum);
    }

    @Getter
    @Setter
    public static class Count {
        private LocalDate serviceDate;
        private DiningType diningType;
        private Integer count;
    }
}
