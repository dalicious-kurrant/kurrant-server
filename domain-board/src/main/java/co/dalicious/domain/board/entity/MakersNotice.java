package co.dalicious.domain.board.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigInteger;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MakersNotice extends BackOfficeNotice {

    @Column(name = "makers_id")
    @Comment("메이커스 ID")
    private BigInteger makersId;
}
