package co.dalicious.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigInteger;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user__oauth_mail")
public class ProviderEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("소셜로그인 가입 리스트 PK")
    private BigInteger Id;

    @Column(name = "provider", nullable = false, length = 16)
    @Comment("소셜로그인 기업 이름")
    private Provider provider;

    @Column(name = "email", nullable = false, length = 64)
    @Comment("소셜로그인 가입된 이메일")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference(value = "user-fk")
    private User user;

    @Builder
    public ProviderEmail(Provider provider, String email, User user) {
        this.provider = provider;
        this.email = email;
        this.user = user;
    }
}
