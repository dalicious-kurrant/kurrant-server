package co.kurrant.app.public_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import java.math.BigInteger;
import java.util.Date;

@Schema(description = "장바구니 담기 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SaveOrderCartDto {
    Integer foodId;
    Integer count;
    Date serviceDate;

    public SaveOrderCartDto(Integer foodId, Date serviceDate, Integer count) {
        this.foodId = foodId;
        this.serviceDate = serviceDate;
        this.count = count;
    }
}
