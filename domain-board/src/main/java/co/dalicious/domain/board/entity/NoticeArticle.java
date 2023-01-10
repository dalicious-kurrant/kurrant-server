package co.dalicious.domain.board.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "board__notice_article")
public class NoticeArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private BigInteger id;

    @CreationTimestamp
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime updated;

    @Column(name = "is_pinned")
    private Boolean isPinned;

    private Integer viewCount;

    private String title;

    @Lob
    private String content;

    @ManyToOne
    @JoinColumn(name="notice_id")
    private Notice notice;

    @Builder
    NoticeArticle(BigInteger id, LocalDateTime created, LocalDateTime updated, Boolean isPinned,
                  Integer viewCount, String title, String content, Notice notice){
        this.id = id;
        this.created = created;
        this.updated = updated;
        this.isPinned = isPinned;
        this.viewCount = viewCount;
        this.title = title;
        this.content = content;
        this.notice = notice;
    }
}
