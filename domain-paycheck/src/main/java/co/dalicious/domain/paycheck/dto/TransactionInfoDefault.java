package co.dalicious.domain.paycheck.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionInfoDefault {
    private String businessNumber;
    private String address1;
    private String address2;
    private String corporationName;
    private String representative;
    private String business;
    private String phone;
    private String faxNumber;
    private String businessForm;

    @Builder

    public TransactionInfoDefault(String businessNumber, String address1, String address2, String corporationName, String representative, String business, String phone, String faxNumber, String businessForm) {
        this.businessNumber = businessNumber;
        this.address1 = address1;
        this.address2 = address2;
        this.corporationName = corporationName;
        this.representative = representative;
        this.business = business;
        this.phone = phone;
        this.faxNumber = faxNumber;
        this.businessForm = businessForm;
    }
}
