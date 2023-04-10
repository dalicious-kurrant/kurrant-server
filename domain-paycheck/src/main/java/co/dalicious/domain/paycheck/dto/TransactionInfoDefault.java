package co.dalicious.domain.paycheck.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionInfoDefault {
    private String businessNumber;
    private String address;
    private String corporationName;
    private String representative;
    private String business;
    private String phone;
    private String faxNumber;
    private String businessForm;

    @Builder
    public TransactionInfoDefault(String businessNumber, String address, String corporationName, String representative, String business, String phone, String faxNumber, String businessForm) {
        this.businessNumber = businessNumber;
        this.address = address;
        this.corporationName = corporationName;
        this.representative = representative;
        this.business = business;
        this.phone = phone;
        this.faxNumber = faxNumber;
        this.businessForm = businessForm;
    }
}
