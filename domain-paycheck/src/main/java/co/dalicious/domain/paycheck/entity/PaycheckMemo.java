package co.dalicious.domain.paycheck.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Embeddable
public class PaycheckMemo {
    @Column(name = "memo_created_date_time", columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @Comment("작성자")
    private String writer;
    @Comment("메모")
    private String memo;

    protected PaycheckMemo() {
        this.createdDateTime = Timestamp.valueOf(LocalDateTime.now());
    }

    public PaycheckMemo(String writer, String memo) {
        this.writer = writer;
        this.memo = memo;
        this.createdDateTime = Timestamp.valueOf(LocalDateTime.now());
    }
}
