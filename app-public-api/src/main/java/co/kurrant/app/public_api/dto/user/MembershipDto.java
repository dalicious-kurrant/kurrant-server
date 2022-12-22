package co.kurrant.app.public_api.dto.user;

import co.dalicious.system.util.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "멤버십 이용내역 조회 응답 DTO")
@Getter
@NoArgsConstructor
public class MembershipDto {
    private BigDecimal price;
    private String startDate;
    private String endDate;
    private int membershipUsingPeriod;

    @Builder
    public MembershipDto(BigDecimal price, LocalDate startDate, LocalDate endDate, int membershipUsingPeriod) {
        this.price = price;
        this.startDate = DateUtils.format(startDate, "yyyy-MM-dd");
        this.endDate = DateUtils.format(endDate, "yyyy-MM-dd");;
        this.membershipUsingPeriod = membershipUsingPeriod;
    }
}
