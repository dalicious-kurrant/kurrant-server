package co.dalicious.domain.board.entity;

import co.dalicious.domain.board.entity.enums.BoardType;
import co.dalicious.domain.file.entity.embeddable.Image;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigInteger;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class MakersNotice extends BackOfficeNotice {

    @Column(name = "makers_id")
    @Comment("메이커스 ID")
    private BigInteger makersId;

    @Builder
    public MakersNotice(String title, String content, Boolean isStatus, BoardType boardType, Boolean isAlarmTalk, BigInteger makersId) {
        super(title, content, isStatus, boardType, isAlarmTalk);
        this.makersId = makersId;
    }
}
