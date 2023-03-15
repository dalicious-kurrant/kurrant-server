package co.dalicious.domain.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Builder
public class GroupInfo {

    private BigInteger groupId;
    private String groupName;

    public static GroupInfo create(BigInteger groupId, String groupName) {
        return GroupInfo.builder()
                .groupId(groupId)
                .groupName(groupName)
                .build();
    }
}
