package co.dalicious.domain.order.dto;

import co.dalicious.system.enums.DiningType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Schema(description = "주문 상세 Dto")
@Getter
@Setter
public class OrderDetailDto {
    private String serviceDate;
    private String diningType;
    private List<OrderItemDto> orderItemDtoList;

    @Getter
    @Setter
    @Builder
    public static class OrderDetail {
        private LocalDate serviceDate;
        private DiningType diningType;

        public OrderDetail(LocalDate serviceDate, DiningType diningType) {
            this.serviceDate = serviceDate;
            this.diningType = diningType;
        }

        public boolean equals(Object obj) {
            if (obj instanceof OrderDetail tmp) {
                return serviceDate.equals(tmp.serviceDate) && diningType.equals(tmp.diningType);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(serviceDate, diningType);
        }
    }
}
