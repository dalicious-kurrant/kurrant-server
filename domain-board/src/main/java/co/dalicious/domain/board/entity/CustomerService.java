package co.dalicious.domain.board.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Column(name="question")
    private String question;

    @Column(name="answer")
    @Lob
    private String answer;

}
