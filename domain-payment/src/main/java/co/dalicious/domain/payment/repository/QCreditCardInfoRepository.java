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


    public long patchDefaultCard(BigInteger cardId, Integer defaultType) {
        return queryFactory.update(creditCardInfo)
                .set(creditCardInfo.defaultType, defaultType)
                .where(creditCardInfo.id.eq(cardId))
                .execute();
    }

    public void deleteCard(BigInteger cardId) {
        queryFactory.delete(creditCardInfo)
                .where(creditCardInfo.id.eq(cardId))
                .execute();
    }

    public List<CreditCardInfo> findAllNotZero(BigInteger id) {
        return queryFactory.selectFrom(creditCardInfo)
                .where(creditCardInfo.user.id.eq(id),
                        creditCardInfo.defaultType.ne(0))
                .fetch();
    }

    public void patchOtherCard(BigInteger id, BigInteger cardId) {
        queryFactory.update(creditCardInfo)
                .set(creditCardInfo.defaultType, 0)
                .where(creditCardInfo.id.ne(cardId),
                        creditCardInfo.id.eq(id))
                .execute();
    }

    public void patchOtherCardAllZero(BigInteger cardId, BigInteger userId) {
        queryFactory.update(creditCardInfo)
                .set(creditCardInfo.defaultType, 0)
                .where(creditCardInfo.id.ne(cardId),
                        creditCardInfo.user.id.eq(userId))
                .execute();
    }

    public void patchTwoToZero(BigInteger cardId, BigInteger userId) {
        queryFactory.update(creditCardInfo)
                .set(creditCardInfo.defaultType, 0)
                .where(creditCardInfo.defaultType.eq(2),
                        creditCardInfo.user.id.eq(userId),
                        creditCardInfo.id.ne(cardId))
                .execute();
    }

    public void patchThreeToZero(BigInteger cardId, BigInteger userId) {
        queryFactory.update(creditCardInfo)
                .set(creditCardInfo.defaultType, 1)
                .where(creditCardInfo.defaultType.eq(3),
                        creditCardInfo.user.id.eq(userId),
                        creditCardInfo.id.ne(cardId))
                .execute();
    }

    public void patchOneToZero(BigInteger cardId, BigInteger userId) {
        queryFactory.update(creditCardInfo)
                .set(creditCardInfo.defaultType, 0)
                .where(creditCardInfo.defaultType.eq(1),
                        creditCardInfo.user.id.eq(userId),
                        creditCardInfo.id.ne(cardId))
                .execute();
    }

    public void patchThreeToTwo(BigInteger cardId, BigInteger userId) {
        queryFactory.update(creditCardInfo)
                .set(creditCardInfo.defaultType, 2)
                .where(creditCardInfo.defaultType.eq(3),
                        creditCardInfo.user.id.eq(userId),
                        creditCardInfo.id.ne(cardId))
                .execute();
    }

    public CreditCardInfo findCustomerKeyByCardId(BigInteger cardId) {
        return queryFactory
                .selectFrom(creditCardInfo)
                .where(creditCardInfo.id.eq(cardId))
                .fetchOne();
    }
}
