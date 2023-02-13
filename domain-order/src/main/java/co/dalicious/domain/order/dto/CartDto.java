package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigInteger;
import java.util.List;

@Schema(description = "장바구니 담기 요청 DTO")
@Getter
public class CartDto {
    BigInteger spotId;
    BigInteger dailyFoodId;
    Integer count;

    @Getter
    public static class Response {
        private Integer cartCount;
        private List<DailyFoodCount> dailyFoodCountList;

        public Response(Integer cartCount, List<DailyFoodCount> dailyFoodCountList) {
            this.cartCount = cartCount;
            this.dailyFoodCountList = dailyFoodCountList;
        }
    }

    @Getter
    public static class DailyFoodCount {
        private BigInteger dailyFoodId;
        private Integer RemainCount;

        public DailyFoodCount(BigInteger dailyFoodId, Integer remainCount) {
            this.dailyFoodId = dailyFoodId;
            RemainCount = remainCount;
        }
    }
}
