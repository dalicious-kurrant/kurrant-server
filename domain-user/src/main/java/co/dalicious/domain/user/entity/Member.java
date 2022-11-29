package co.dalicious.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.sql.Timestamp;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name="member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private int id;

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(32)")
    @Comment("사용자 명")
    private String name;

    @Column(name = "created", nullable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    private Timestamp created;
}
