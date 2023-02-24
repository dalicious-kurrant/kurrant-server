package co.kurrant.app.admin_api.dto.schedules;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.dto.PresetScheduleResponseDto;
import co.dalicious.domain.food.entity.Makers;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class ScheduleResponseDto {
    private List<GroupInfo> groupInfoList;
    private List<MakersInfo> makersInfoList;
    private List<PresetScheduleResponseDto> presetScheduleList;

    @Getter
    @Builder
    public static class GroupInfo {
        private BigInteger groupId;
        private String groupName;

        public static GroupInfo createdGroupInfo(BigInteger groupId, String groupName) {
            return GroupInfo.builder()
                    .groupId(groupId)
                    .groupName(groupName)
                    .build();
        }

        public static List<GroupInfo> createdGroupInfoList(List<Group> groupList) {
            List<GroupInfo> groupInfos = new ArrayList<>();
            for(Group group : groupList) {
                GroupInfo groupInfo = GroupInfo.createdGroupInfo(group.getId(), group.getName());
                groupInfos.add(groupInfo);
            }
            return groupInfos;
        }
    }

    @Getter
    @Builder
    public static class MakersInfo {
        private BigInteger makersId;
        private String makersName;

        public static MakersInfo createdMakersInfo(BigInteger makersId, String makersName) {
            return MakersInfo.builder()
                    .makersId(makersId)
                    .makersName(makersName)
                    .build();
        }

        public static List<MakersInfo> createdMakersInfoList(List<Makers> makersList) {
            List<MakersInfo> makersInfos = new ArrayList<>();
            for(Makers makers : makersList) {
                MakersInfo makersInfo = MakersInfo.createdMakersInfo(makers.getId(), makers.getName());
                makersInfos.add(makersInfo);
            }
            return makersInfos;
        }
    }

    public static ScheduleResponseDto createdResponseDto(List<Group> groups, List<Makers> makers, List<PresetScheduleResponseDto> presetScheduleList) {
        List<GroupInfo> groupInfoList = GroupInfo.createdGroupInfoList(groups);
        List<MakersInfo> makersInfoList = MakersInfo.createdMakersInfoList(makers);
        return ScheduleResponseDto.builder()
                .groupInfoList(groupInfoList)
                .makersInfoList(makersInfoList)
                .presetScheduleList(presetScheduleList).build();
    }
}
