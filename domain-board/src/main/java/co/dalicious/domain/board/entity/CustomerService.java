package co.dalicious.domain.board.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board__customer_service")
public class CustomerService {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private Timestamp updatedDateTime;

    @Column(name="title")
    @Comment("FAQ 제목")
    private String title;

    @Column(name="title_no")
    @Comment("FAQ 번호")
    private Integer titleNo;

    @Comment("1:title/2:question/3:answer")
    private Integer type;

    @Column(name="question")
    @Comment("FAQ 질문")
    private String question;

    @Column(name="answer")
    @Comment("FAQ 답변")
    @Lob
    private String answer;

    @Builder
    CustomerService(BigInteger id, String title,
                    Integer titleNo, Integer type, String question, String answer){
        this.id = id;
        this.title = title;
        this.titleNo = titleNo;
        this.type = type;
        this.question = question;
        this.answer = answer;
    }

}
