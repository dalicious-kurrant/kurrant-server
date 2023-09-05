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
    @Column(name = "status", columnDefinition = "INT", nullable = false)
    private Integer status;

    @Comment(value = "디폴트 타입, 1:기본 결제카드, 2:멤버십 결제카드, 0:아무것도 아님, 3:기본과 멤버십 결제 모두")
    @Column(name="default_type", columnDefinition = "VARCHAR(16)")
    private Integer defaultType;

    @ManyToOne(optional = false)
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="customer_key", columnDefinition = "VARCHAR(64)")
    private String customerKey;

    @Column(name="toss_billing_key", columnDefinition = "VARCHAR(64)")
    private String tossBillingKey;

    @Column(name="nice_billing_key", columnDefinition = "VARCHAR(64)")
    private String niceBillingKey;

    @Column(name="mingle_billing_key", columnDefinition = "VARCHAR(64)")
    private String mingleBillingKey;


    @Builder
    CreditCardInfo(String cardNumber, User user, String ownerType,
                   String cardType, String customerKey, String tossBillingKey,
                   String niceBillingKey,
                   String cardCompany, Integer defaultType, Integer status){
        this.cardNumber = cardNumber;
        this.user = user;
        this.ownerType = ownerType;
        this.cardType = cardType;
        this.customerKey = customerKey;
        this.tossBillingKey = tossBillingKey;
        this.niceBillingKey = niceBillingKey;
        this.cardCompany = cardCompany;
        this.defaultType = defaultType;
        this.status = status;
    }

    public CreditCardInfo(Integer status) {
        this.status = status;
    }

    public Boolean isSameCard(String cardNumber, String cardCompany) {
        if(this.niceBillingKey == null) {
            return false;
        }
        return this.cardNumber.equals(cardNumber) && this.cardCompany.equals(cardCompany);
    }

    public void updateNiceBillingKey(String niceBillingKey) {
        this.niceBillingKey = niceBillingKey;
    }

    public void updateMingleBillingKey(String mingleBillingKey) {
        this.mingleBillingKey = mingleBillingKey;
    }

    public void updateStatus(Integer status) {
        this.status = status;
    }

    public void updateTossBillingKey(String tossBillingKey) {
        this.tossBillingKey = tossBillingKey;
    }
}
