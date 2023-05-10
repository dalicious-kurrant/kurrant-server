package co.dalicious.domain.order.dto.point;

import co.dalicious.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class FoundersPointDto {
    private User user;
    private LocalDate serviceDate;
    private LocalDate foundersStartDate;

    public static FoundersPointDto create (User user, LocalDate serviceDate, LocalDate foundersStartDate) {
        return FoundersPointDto.builder()
                .user(user)
                .serviceDate(serviceDate)
                .foundersStartDate(foundersStartDate)
                .build();
    }

    public BigInteger getUserId() {
        return user.getId();
    }
}
