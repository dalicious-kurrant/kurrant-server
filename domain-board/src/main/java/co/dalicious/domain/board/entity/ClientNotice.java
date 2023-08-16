package co.dalicious.domain.board.entity;

import co.dalicious.domain.board.converter.GroupIdListConverter;
import co.dalicious.domain.board.entity.enums.BoardType;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import java.math.BigInteger;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class ClientNotice extends BackOfficeNotice {

    @Column(name = "group_ids")
    @Convert(converter = GroupIdListConverter.class)
    @Comment("그룹ID List")
    private List<BigInteger> groupIds;

    @Builder
    public ClientNotice(String title, String content, Boolean isStatus, BoardType boardType, Boolean isAlarmTalk, List<BigInteger> groupIds) {
        super(title, content, isStatus, boardType, isAlarmTalk);
        this.groupIds = groupIds;
    }
}
