package co.kurrant.app.admin_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Schema(description = "그룹 조회 DTO")
public class GroupDto {
    private Group group;
    private List<Spot> spots;
    private List<DiningType> diningTypes;
    private List<User> users;

    @Getter
    @Setter
    public static class GroupAndMakers {
        private List<MakersDto.Makers> makers;
        private List<Group> groups;
    }

    @Setter
    @Getter
    public static class Group {
        private BigInteger groupId;
        private String groupName;
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
