package co.dalicious.domain.food.repository;

import co.dalicious.domain.food.entity.Makers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

public interface MakersRepository extends JpaRepository<Makers, BigInteger> {
    Makers findByCode(String code);
    Makers findByName(String name);
/*
    @Transactional
    @Query(value = "INSERT INTO makers__makers VALUES(CEO = :#{#makers.ceo}, ceophone=#{#makers.ceoPhone}, account_number=#{#makers.accountNumber}, address_depth_1=#{#makers.address.address1}, address_depth_2, address_location, zip_code, bank, close_time, code, company_name, company_registration_number, contract_end_date, contract_start_date, created_datetime, deposit_holder, img_created_datetime, filename, s3_key, file_location, is_nutrition_information, is_parent_company, manager_name, manager_phone, name, open_time, parent_company_id, password, e_role, e_service_form, e_service_type, updated_datetime)", nativeQuery = true)
    Makers savePoint(@Param("makers") Makers makers);*/
}
