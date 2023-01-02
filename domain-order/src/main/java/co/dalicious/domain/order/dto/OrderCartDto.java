package co.dalicious.domain.order.dto;

import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DiningType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Schema(description = "장바구니 담기 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCartDto {
    Integer dailyFoodId;
    Integer count;
    String serviceDate;
    DiningType diningType;


    @Builder
    public OrderCartDto(Integer dailyFoodId, LocalDate serviceDate, Integer count, DiningType diningType) {
        this.dailyFoodId = dailyFoodId;
        this.serviceDate = DateUtils.format(serviceDate, "yyyy-MM-dd");
        this.count = count;
        this.diningType = diningType;
    }
}
