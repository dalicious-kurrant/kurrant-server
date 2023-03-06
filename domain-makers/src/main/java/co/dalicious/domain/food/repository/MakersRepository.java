package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.Makers;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface MakersRepository extends JpaRepository<Makers, BigInteger> {
    Makers findByCode(String code);
    Makers findByName(String name);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO makers__makers(makers__makers.ceo, sjtest.makers__makers.ceophone, sjtest.makers__makers.account_number, sjtest.makers__makers.address_depth_1, sjtest.makers__makers.address_depth_2, sjtest.makers__makers.address_location, sjtest.makers__makers.zip_code, sjtest.makers__makers.bank, sjtest.makers__makers.code, sjtest.makers__makers.company_name, sjtest.makers__makers.company_registration_number, sjtest.makers__makers.contract_end_date, sjtest.makers__makers.contract_start_date, sjtest.makers__makers.deposit_holder, sjtest.makers__makers.is_nutrition_information, sjtest.makers__makers.is_parent_company, sjtest.makers__makers.manager_name, sjtest.makers__makers.manager_phone, sjtest.makers__makers.name, sjtest.makers__makers.open_time, sjtest.makers__makers.close_time, sjtest.makers__makers.parent_company_id, service_form, service_type) VALUES(ceo = :ceo, ceophone= :ceoPhone, account_number= :accountNumber, address_depth_1=:address1, address_depth_2= :address2, address_location=ST_GEOMFROMTEXT('POINT('+:location+')'), zip_code=:zipCode, bank=:bank, code=:code, company_name=:companyName, company_registration_number=:registryNumber, contract_end_date=:contractEndDate, contract_start_date=:contractStartDate, deposit_holder=:depositHolder, is_nutrition_information=:isNutritionInformation, is_parent_company=:isParentCompany, manager_name=:managerName, manager_phone=:managerPhone, name=:name, open_time=:openTime, close_time=:closeTime, parent_company_id=:parentCompanyId, e_service_form=:serviceForm, e_service_type=:serviceType)", nativeQuery = true)
    void savePoint(@Param("ceo") String ceo, @Param("ceoPhone") String ceoPhone, @Param("accountNumber") String accountNumber, @Param("address1") String address1, @Param("address2") String address2,
                   @Param("location") Point location , @Param("zipCode") String zipCode, @Param("bank") String bank, @Param("code") String code, @Param("companyName") String companyName, @Param("registryNumber") String registryNumber,
                   @Param("contractEndDate") LocalDate contractEndDate, @Param("contractStartDate") LocalDate contractStartDate, @Param("depositHolder") String depositHolder, @Param("isNutritionInformation") Boolean isNutritionInformation, @Param("isParentCompany") Boolean isParentCompany,
                   @Param("managerName") String managerName, @Param("managerPhone") String managerPhone, @Param("name")String name, @Param("openTime") LocalTime openTime, @Param("closeTime") LocalTime closeTime, @Param("parentCompanyId") BigInteger parentCompanyId, @Param("serviceForm")Integer serviceForm, @Param("serviceType") Integer serviceType);

}
