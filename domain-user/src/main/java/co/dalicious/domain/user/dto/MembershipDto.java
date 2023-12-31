package co.dalicious.domain.user.dto;

import co.dalicious.system.util.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Schema(description = "멤버십 이용내역 조회 응답 DTO")
@Getter
@Setter
public class MembershipDto {
    private BigInteger id;
    private String membershipSubscriptionType;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private String startDate;
    private String endDate;
    private int membershipUsingPeriod;

    @Builder
    public MembershipDto(BigInteger id, String membershipSubscriptionType, BigDecimal price, BigDecimal discountedPrice, LocalDate startDate, LocalDate endDate, int membershipUsingPeriod) {
        this.id = id;
        this.membershipSubscriptionType = membershipSubscriptionType;
        this.price = price;
        this.discountedPrice = discountedPrice;
        this.startDate = DateUtils.format(startDate, "yyyy-MM-dd");
        this.endDate = DateUtils.format(endDate, "yyyy-MM-dd");;
        this.membershipUsingPeriod = membershipUsingPeriod;
    }
}
