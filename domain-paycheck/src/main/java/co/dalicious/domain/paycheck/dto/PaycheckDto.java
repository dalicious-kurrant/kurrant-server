package co.dalicious.domain.paycheck.dto;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.system.enums.DiningType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

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
    public static class MakersDetail {
        private TransactionInfoDefault transactionInfoDefault;
        private MakersPaycheckInfo makersPaycheckInfo;
        private List<PaycheckDailyFoodDto> paycheckDailyFoods;
        private List<PaycheckAddDto> paycheckAdds;
        private Integer foodsPrice;
        private Double commission;
        private Integer commissionPrice;
        private Integer totalPrice;
        private List<String> paycheckMemo;
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

    @Getter
    @Setter
    public static class PaycheckDailyFood {
        private Makers makers;
        private DiningType diningType;
        private LocalDate serviceDate;
        private Food food;
        private String foodName;
        private BigDecimal supplyPrice;
        private Integer count;
    }

    @Getter
    @Setter
    public static class PaycheckDailyFoodDto {
        private String serviceDate;
        private String foodName;
        private BigDecimal supplyPrice;
        private Integer count;
        private BigDecimal totalPrice;
    }

    @Getter
    @Setter
    public static class PaycheckAddDto {
        private String issueDate;
        private BigDecimal price;
        private String memo;
    }

    @Getter
    @Setter
    public static class MakersPaycheckInfo {
        private String year;
        private String month;
        private String makers;
        private String status;
        private String depositHolder;
        private String bankName;
        private String bankAccount;

        @Builder
        public MakersPaycheckInfo(String year, String month, String makers, String status, String depositHolder, String bankName, String bankAccount) {
            this.year = year;
            this.month = month;
            this.makers = makers;
            this.status = status;
            this.depositHolder = depositHolder;
            this.bankName = bankName;
            this.bankAccount = bankAccount;
        }
    }
}
