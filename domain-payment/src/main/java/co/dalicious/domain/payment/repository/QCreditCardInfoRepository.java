package co.dalicious.domain.payment.repository;

import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static co.dalicious.domain.payment.entity.QCreditCardInfo.creditCardInfo;

@Repository
@RequiredArgsConstructor
public class QCreditCardInfoRepository {
    public final JPAQueryFactory queryFactory;


//    public void save(CreditCardSaveDto creditCardSaveDto) {
//        queryFactory.insert(creditCardInfo)
//                .columns(creditCardInfo.cardNumber, creditCardInfo.user, creditCardInfo.ownerType,
//                        creditCardInfo.cardType, creditCardInfo.customerKey, creditCardInfo.billingKey,
//                        creditCardInfo.cardCompany)
//                .values(creditCardSaveDto.getCardNumber(), creditCardSaveDto.getUserId(), creditCardSaveDto.getOwnerType(),
//                        creditCardSaveDto.getCardType(), creditCardSaveDto.getCustomerKey(), creditCardSaveDto.getBillingKey(),
//                        creditCardSaveDto.getCardCompany())
//                .execute();
//    }

    public void saveCreditCard(String cardNumber, User userId, String ownerType,
                               String cardType, String customerKey, String billingKey,
                               String cardCompany) {
        queryFactory.insert(creditCardInfo)
                .set(creditCardInfo.cardNumber, cardNumber)
                .set(creditCardInfo.cardType, cardType)
                .set(creditCardInfo.user, userId)
                .set(creditCardInfo.billingKey, billingKey)
                .set(creditCardInfo.cardCompany, cardCompany)
                .set(creditCardInfo.customerKey, customerKey)
                .set(creditCardInfo.ownerType, ownerType)
                .execute();
    }

    public List<CreditCardInfo> findAllByUserId(BigInteger id) {
        return queryFactory.selectFrom(creditCardInfo)
                .where(creditCardInfo.user.id.eq(id))
                .fetch();
    }
}
