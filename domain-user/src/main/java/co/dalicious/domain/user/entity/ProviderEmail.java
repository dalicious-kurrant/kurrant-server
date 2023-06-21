package co.dalicious.domain.user.entity;

import co.dalicious.domain.user.entity.enums.Provider;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user__oauth_mail", uniqueConstraints={@UniqueConstraint(columnNames={"provider", "email"})})
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

    @ManyToOne(optional = false)
    @JoinColumn
    @JsonManagedReference(value = "user-fk")
    private User user;

    @Builder
    public ProviderEmail(Provider provider, String email, User user) {
        this.provider = provider;
        this.email = email;
        this.user = user;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void withdrawEmail() {
        if (this.email != null && email.contains("@") && email.indexOf("@") >= 2) {
            // extract the email prefix and domain
            String prefix = this.email.substring(0, 2);
            String domain = this.email.substring(this.email.indexOf("@"));
            // replace the prefix with asterisks
            String maskedPrefix = "*".repeat(this.email.substring(2, this.email.indexOf("@")).length());
            // set the modified email address
            double dValue = Math.random();
            int iValue = (int)(dValue * 10000);
            this.email = prefix + maskedPrefix + domain + "(" + iValue + ")";
        } else {
            this.email = email; // use the original email if it doesn't meet the criteria
        }
    }
}
