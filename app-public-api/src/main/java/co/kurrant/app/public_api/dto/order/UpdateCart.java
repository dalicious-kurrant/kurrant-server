package co.kurrant.app.public_api.dto.order;

import lombok.Data;

@Data
public class UpdateCart {
    private Integer dailyFoodId;
    private Integer count;
}
