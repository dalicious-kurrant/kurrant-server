package co.dalicious.domain.board.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board__notice")
public class Notice {
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

    @Column(name="name")
    private String title;

    @Column(name="content")
    @Lob
    private String content;

    @Column(name="type")
    @Comment("1:전체공지/ 2:스팟공지")
    private Integer type;

    @Builder
    public Notice(BigInteger id, LocalDate created, LocalDate updated, String title, String content, Integer type){
        this.id = id;
        this.created = created;
        this.updated = updated;
        this.title = title;
        this.content = content;
        this.type = type;
    }


}
