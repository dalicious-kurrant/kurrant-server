package co.dalicious.domain.user.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "user__creditcard_info")
public class CreditCardInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Comment(value = "카드 번호")
    @Column(name="card_number", columnDefinition = "VARCHAR(16)")
    private String cardNumber;

    @Comment(value = "카드 유효기간 년")
    @Column(name="expiration_year", columnDefinition = "VARCHAR(8)")
    private String expirationYear;

    @Comment(value = "카드 유효기간 월")
    @Column(name="expiration_month", columnDefinition = "VARCHAR(8)")
    private String expirationMonth;

    @Comment(value = "카드검증번호 CVC")
    @Column(name="card_validation_code", columnDefinition = "VARCHAR(8)")
    private String cardValidationCode;

    @Column(name="user_identity_number", columnDefinition = "VARCHAR(8)")
    private String identityNumber;

    @Comment(value = "0:개인카드/1:법인카드")
    @Column(name="card_type", columnDefinition = "INT(8)")
    private Integer type;

    @ManyToOne(optional = false)
    @JoinColumn(name="user_id")
    private User user;

    @Builder
    CreditCardInfo(BigInteger id, String cardNumber, String expirationYear, String expirationMonth, String identityNumber,
                   String cardValidationCode, Integer type, User user){
        this.id = id;
        this.cardNumber = cardNumber;
        this.expirationYear = expirationYear;
        this.expirationMonth = expirationMonth;
        this.identityNumber= identityNumber;
        this.cardValidationCode = cardValidationCode;
        this.type = type;
        this.user = user;
    }
}
