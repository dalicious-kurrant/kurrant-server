package co.dalicious.domain.food.repository;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.food.dto.MakersCapacityDto;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.QMakers;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static co.dalicious.domain.food.entity.QMakers.makers;

@Repository
@RequiredArgsConstructor
public class QMakersRepository {

    private final JPAQueryFactory queryFactory;

    public Makers findOneByCode(String code) {
        return queryFactory.selectFrom(makers)
                .where(makers.code.eq(code))
                .fetchOne();
    }

    public List<Makers> getMakersByName(Set<String> makersName) {
        return queryFactory.selectFrom(makers)
                .where(makers.name.in(makersName))
                .fetch();
    }

    public void updateMakers(BigInteger id, String code, String name, String companyName, String ceo, String ceoPhone, String managerName, String managerPhone, List<MakersCapacityDto> diningTypes, Integer dailyCapacity, String serviceType, String serviceForm, Boolean isParentCompany, BigInteger parentCompanyId, Address address, String companyRegistrationNumber, String contractStartDate, String contractEndDate, Boolean isNutritionInformation, String openTime, String closeTime, String bank, String depositHolder, String accountNumber) {
        //코드수정
        if (!code.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.code, code)
                    .where(makers.id.eq(id))
                    .execute();
        }
        //이름 수정
        if (!name.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.name, name)
                    .where(makers.id.eq(id))
                    .execute();
        }

        //companyName수정
        if (!companyName.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.companyName, companyName)
                    .where(makers.id.eq(id))
                    .execute();
        }
        //ceo 수정
        if (!ceo.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.CEO, ceo)
                    .where(makers.id.eq(id))
                    .execute();
        }
        //ceoPhone 수정
        if (!ceoPhone.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.CEOPhone, ceoPhone)
                    .where(makers.id.eq(id))
                    .execute();
        }
        //managerName 수정
        if (!managerName.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.managerName, managerName)
                    .where(makers.id.eq(id))
                    .execute();
        }
        //managerPhone 수정
        if (!managerPhone.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.managerPhone, managerPhone)
                    .where(makers.id.eq(id))
                    .execute();
        }

    }
}
