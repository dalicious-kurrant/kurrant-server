package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "특정 날짜와 식사타입에 해당하는 주문내역 조회")
public class OrderByDateAndDiningTypeResDto {

    private String makersName;
    private String foodName;
    private String spotName;
    private Integer count;
    private String imageLocation;

}
