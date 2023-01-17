package co.dalicious.domain.board.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board__customer_service")
public class CustomerService {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @CreatedDate
    @Column(name="created")
    private LocalDate created;

    @LastModifiedDate
    @Column(name="updated")
    private LocalDate updated;

    @Column(name="title")
    private String title;

    @Column(name="title_no")
    private Integer titleNo;

    @Comment("1:title/2:question/3:question")
    private Integer type;

    @Column(name="question")
    private String question;

    @Column(name="answer")
    @Lob
    private String answer;

    @Builder
    CustomerService(BigInteger id, LocalDate created, LocalDate updated, String title,
                    Integer titleNo, Integer type, String question, String answer){
        this.id = id;
        this.created = created;
        this.updated = updated;
        this.title = title;
        this.titleNo = titleNo;
        this.type = type;
        this.question = question;
        this.answer = answer;
    }

}
