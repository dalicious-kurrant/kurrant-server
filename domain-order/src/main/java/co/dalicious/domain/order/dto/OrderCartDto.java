package co.dalicious.domain.order.dto;

import co.dalicious.system.util.DiningType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Schema(description = "장바구니 담기 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCartDto {
    Integer foodId;
    Integer count;
    LocalDate serviceDate;
    DiningType diningType;


    public OrderCartDto(Integer foodId, LocalDate serviceDate, Integer count, DiningType diningType) {
        this.foodId = foodId;
        this.serviceDate = serviceDate;
        this.count = count;
        this.diningType = diningType;
    }
}
