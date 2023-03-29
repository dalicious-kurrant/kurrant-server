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
        private Integer year;
        private Integer month;
        private String corporationName;
        private String accountHolder;
        private String nameOfBank;
        private String accountNumber;
        private Integer paycheckStatus;
    }
    @Getter
    @Setter
    public static class CorporationRequest {
        private Integer year;
        private Integer month;
        private String corporationName;
        private String accountHolder;
        private String nameOfBank;
        private String accountNumber;
        private Integer paycheckStatus;
    }
}
