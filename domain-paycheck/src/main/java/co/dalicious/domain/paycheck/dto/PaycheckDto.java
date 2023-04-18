package co.dalicious.domain.paycheck.dto;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.system.enums.DiningType;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.hpsf.Decimal;

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
        private List<PaycheckDailyFoodDto> paycheckDailyFoods;
        private List<PaycheckAddDto> paycheckAdds;
        private BigDecimal foodsPrice;
        private Double commission;
        private BigDecimal totalPrice;
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
        private String issueItem;
        private String paycheckItem;
        private BigDecimal price;
        private String memo;
    }
}
