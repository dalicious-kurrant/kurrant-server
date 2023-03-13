package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Schema(description = "그룹 조회 DTO")
public class GroupDto {
    private List<Spot> spots;
    private List<Makers> makers;
    private List<DiningType> diningTypes;
    private List<User> users;

    @Setter
    @Getter
    public static class Makers {
        private BigInteger makersId;
        private String makersName;
    }

    @Setter
    @Getter
    public static class Spot {
        private BigInteger spotId;
        private String spotName;
    }

    @Setter
    @Getter
    public static class DiningType {
        private Integer code;
        private String diningType;
    }

    @Setter
    @Getter
    public static class User {
        private BigInteger userId;
        private String userName;
    }

}
