package co.dalicious.domain.paycheck.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

public class PaycheckDto {
    @Getter
    @Setter
    public static class MakersRequest {
        private BigInteger makersId;
        private Integer year;
        private Integer month;
        private Integer paycheckStatus;
    }

    @Getter
    @Setter
    public static class MakersResponse {
        private BigInteger id;
        private Integer year;
        private Integer month;
        private String makersName;
        private String accountHolder;
        private String nameOfBank;
        private String accountNumber;
        private String paycheckStatus;
        private String excelFile;
        private String pdfFile;
    }
    @Getter
    @Setter
    public static class CorporationRequest {
        private BigInteger corporationId;
        private Integer year;
        private Integer month;
        private String managerName;
        private String phone;
        private Integer paycheckStatus;
    }

    @Getter
    @Setter
    public static class CorporationResponse {
        private BigInteger id;
        private Integer year;
        private Integer month;
        private String corporationName;
        private String managerName;
        private String phone;
        private String paycheckStatus;
        private String excelFile;
        private String pdfFile;
    }
}
