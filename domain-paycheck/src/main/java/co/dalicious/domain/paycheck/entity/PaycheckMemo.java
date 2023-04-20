package co.dalicious.domain.paycheck.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class PaycheckMemo {
    @Comment("작성자")
    private String writer;
    @Comment("메모")
    private String memo;
}
