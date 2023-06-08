package co.dalicious.domain.order.dto;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "주문 고객 정보 DTO")
public class OrderUserInfoDto {
    private String groupName;
    private String spotName;
    private Address address;
    private User user;
}
