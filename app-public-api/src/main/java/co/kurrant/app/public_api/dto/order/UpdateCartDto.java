package co.kurrant.app.public_api.dto.order;

import lombok.Getter;

@Getter
public class UpdateCartDto {
    private Integer foodId;
    private Integer count;
}
