package co.dalicious.domain.payment.entity;

import co.dalicious.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "user__creditcard_info")
public class CreditCardInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Comment(value = "카드 번호")
    @Column(name="card_number", columnDefinition = "VARCHAR(16)")
    private String cardNumber;

    @Comment(value = "카드 회사")
    @Column(name="card_company", columnDefinition = "VARCHAR(16)")
    private String cardCompany;

    @Comment(value = "개인/법인")
    @Column(name="owner_type", columnDefinition = "VARCHAR(16)")
    private String ownerType;

    @Comment(value = "체크/신용")
    @Column(name="card_type", columnDefinition = "VARCHAR(16)")
    private String cardType;

    @Comment(value = "카드 상태, 0:삭제된카드, 1:사용중인 카드")
    @Column(name = "status", columnDefinition = "INT")
    private Integer status;

    @Comment(value = "디폴트 타입, 1:기본 결제카드, 2:멤버십 결제카드, 0:아무것도 아님")
    @Column(name="default_type", columnDefinition = "VARCHAR(16)")
    private Integer defaultType;

    @ManyToOne(optional = false)
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="customer_key", columnDefinition = "VARCHAR(64)")
    private String customerKey;

    @Column(name="billing_key", columnDefinition = "VARCHAR(64)")
    private String billingKey;


    @Builder
    CreditCardInfo(String cardNumber, User user, String ownerType,
                   String cardType, String customerKey, String billingKey,
                   String cardCompany, Integer defaultType){
        this.cardNumber = cardNumber;
        this.user = user;
        this.ownerType = ownerType;
        this.cardType = cardType;
        this.customerKey = customerKey;
        this.billingKey = billingKey;
        this.cardCompany = cardCompany;
        this.defaultType = defaultType;
    }
}
