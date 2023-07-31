package co.dalicious.domain.board.entity;

import co.dalicious.domain.board.converter.GroupIdListConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import java.math.BigInteger;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ClientNotice extends BackOfficeNotice{

    @Column(name = "group_ids")
    @Convert(converter = GroupIdListConverter.class)
    @Comment("그룹ID List")
    private List<BigInteger> groupIds;
}
