package co.dalicious.domain.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "메이커스 정보 조회 응답 DTO")
public class MakersInfoResponseDto {

    private BigInteger id;
    private String code;
    private String name;
    private String companyName;
    private String ceo;
    private String ceoPhone;
    private String managerName;
    private String managerPhone;
    private Integer dailyCapacity;
    private Integer serviceType;
    private String serviceForm;
    private Boolean isParentCompany;
    private BigInteger parentCompanyId;
    private String zipCode;
    private String address1;
    private String address2;
    private String location;
    private String companyRegistrationNumber;
    private String contractStartDate;
    private String contractEndDate;
    private LocalDate isNutritionInformation;
    private String openTime;
    private String closeTime;
    private String bank;
    private String depositHolder;
    private String accountNumber;
    private String createdDateTime;
    private String updatedDateTime;

}
