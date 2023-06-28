package co.dalicious.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class UserGroupDto {
    private Integer mySpotCount;
    private Integer shareSpotCount;
    private Integer privateSpotCount;
    private String defaultSpotName;
    private List<GroupInfo> groups;

    @Getter
    @Setter
    public static class GroupInfo {
        private BigInteger groupId;
        private String groupType;
        private String groupName;
    }
}
