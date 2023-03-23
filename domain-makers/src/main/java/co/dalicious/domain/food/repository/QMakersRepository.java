package co.dalicious.domain.food.repository;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.food.dto.LocationTestDto;
import co.dalicious.domain.food.dto.MakersCapacityDto;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.MakersCapacity;
import co.dalicious.domain.food.entity.QMakers;
import co.dalicious.domain.food.entity.enums.ServiceForm;
import co.dalicious.domain.food.entity.enums.ServiceType;
import co.dalicious.domain.food.mapper.MakersCapacityMapper;
import co.dalicious.system.enums.DiningType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static co.dalicious.domain.food.entity.QMakers.makers;

@Repository
@RequiredArgsConstructor
public class QMakersRepository {

    private final JPAQueryFactory queryFactory;
    private final MakersCapacityMapper makersCapacityMapper;

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

    public void updateMakers(BigInteger id, String code, String name,
                             String companyName, String ceo, String ceoPhone,
                             String managerName, String managerPhone, String serviceType,
                             String serviceForm, Boolean isParentCompany, BigInteger parentCompanyId,
                             Address address, String companyRegistrationNumber, String contractStartDate,
                             String contractEndDate, Boolean isNutritionInformation, String openTime,
                             String closeTime, String bank, String depositHolder, String accountNumber, String fee) {
        //코드수정
        if (code != null && !code.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.code, code)
                    .where(makers.id.eq(id))
                    .execute();
        }
        //이름 수정
        if (name != null && !name.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.name, name)
                    .where(makers.id.eq(id))
                    .execute();
        }

        //companyName수정
        if (companyName != null && !companyName.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.companyName, companyName)
                    .where(makers.id.eq(id))
                    .execute();
        }
        //ceo 수정
        if (ceo != null && !ceo.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.CEO, ceo)
                    .where(makers.id.eq(id))
                    .execute();
        }
        //ceoPhone 수정
        if (ceoPhone != null && !ceoPhone.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.CEOPhone, ceoPhone)
                    .where(makers.id.eq(id))
                    .execute();
        }
        //managerName 수정
        if (managerName != null && !managerName.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.managerName, managerName)
                    .where(makers.id.eq(id))
                    .execute();
        }
        //managerPhone 수정
        if (managerPhone != null && !managerPhone.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.managerPhone, managerPhone)
                    .where(makers.id.eq(id))
                    .execute();
        }

        //서비스타입
        if (serviceType != null && !serviceType.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.serviceType, ServiceType.ofString(serviceType))
                    .where(makers.id.eq(id))
                    .execute();
        }
        //서비스 폼
        if (serviceForm != null && !serviceForm.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.serviceForm, ServiceForm.ofString(serviceForm))
                    .where(makers.id.eq(id))
                    .execute();
        }
        //모회사여부
        if (isParentCompany != null){
            queryFactory.update(makers)
                    .set(makers.isParentCompany, isParentCompany)
                    .where(makers.id.eq(id))
                    .execute();
        }
        //모회사 아이디
        if (parentCompanyId != null){
            queryFactory.update(makers)
                    .set(makers.parentCompanyId, parentCompanyId)
                    .where(makers.id.eq(id))
                    .execute();
        }

        //address 수정
        if (address != null){
            queryFactory.update(makers)
                    .set(makers.address.address1, address.getAddress1())
                    .set(makers.address.address2, address.getAddress2())
                    .set(makers.address.zipCode, address.getZipCode())
                    .set(makers.address.location, address.getLocation())
                    .where(makers.id.eq(id))
                    .execute();
        }
        //사업자번호 수정
        if (companyRegistrationNumber != null && !companyRegistrationNumber.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.companyRegistrationNumber, companyRegistrationNumber)
                    .where(makers.id.eq(id))
                    .execute();
        }
        //계약 시작일 수정
        if (!contractStartDate.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.contractStartDate, LocalDate.parse(contractStartDate))
                    .where(makers.id.eq(id))
                    .execute();
        }
        //계약 종료일 수정
        if (contractEndDate != null && !contractEndDate.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.contractEndDate, LocalDate.parse(contractEndDate))
                    .where(makers.id.eq(id))
                    .execute();
        }
        //isNutritionInformation 수정
        if (isNutritionInformation != null){
            queryFactory.update(makers)
                    .set(makers.isNutritionInformation, isNutritionInformation)
                    .where(makers.id.eq(id))
                    .execute();
        }
        //오픈시간 수정
        if (openTime != null && !openTime.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.openTime, LocalTime.parse(openTime))
                    .where(makers.id.eq(id))
                    .execute();
        }
        //종료시간 수정
        if (closeTime != null && !closeTime.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.closeTime, LocalTime.parse(closeTime))
                    .where(makers.id.eq(id))
                    .execute();
        }
        //은행 수정
        if (bank != null && !bank.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.bank, bank)
                    .where(makers.id.eq(id))
                    .execute();
        }
        //예금주 수정
        if (depositHolder != null && !depositHolder.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.depositHolder, depositHolder)
                    .where(makers.id.eq(id))
                    .execute();
        }
        //계좌번호 수정
        if (accountNumber != null && !accountNumber.isEmpty()){
            queryFactory.update(makers)
                    .set(makers.accountNumber, accountNumber)
                    .where(makers.id.eq(id))
                    .execute();
        }

        //시스템 사용료 수정
        if (fee != null){
            queryFactory.update(makers)
                    .set(makers.fee, fee)
                    .where(makers.id.eq(id))
                    .execute();
        }
    }

    public List<Makers> findMakersListById(Set<BigInteger> makersId) {
        return queryFactory.selectFrom(makers)
                .where(makers.id.in(makersId))
                .fetch();
    }

    public void updateLocation(Geometry location, LocationTestDto locationTestDto) {
        queryFactory.update(makers)
                .set(makers.address.location, location)
                .where(makers.id.eq(locationTestDto.getId()))
                .execute();
    }

    public List<Makers> findByIdList(List<BigInteger> makersIds) {
        return queryFactory.selectFrom(makers)
                .where(makers.id.in(makersIds))
                .fetch();
    }
}
