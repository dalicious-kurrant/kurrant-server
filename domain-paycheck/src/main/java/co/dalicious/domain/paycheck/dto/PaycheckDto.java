package co.dalicious.domain.paycheck.dto;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.paycheck.entity.PaycheckMemo;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
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
        private List<MakersList> makersLists;
        private PaycheckPrice paycheckPrice;
    }

    @Getter
    @Setter
    public static class PaycheckPrice {
        private Integer totalPrice;
        private Integer totalCount;
        private Integer completePrice;
        private Integer completeCount;
        private Integer leftPrice;
        private Integer leftCount;
    }

    @Getter
    @Setter
    public static class MakersList {
        private BigInteger id;
        private Integer year;
        private Integer month;
        private String makersName;
        private Integer totalPrice;
        private String accountHolder;
        private String nameOfBank;
        private String accountNumber;
        private String paycheckStatus;
        private Boolean hasRequest;
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
        private List<MemoResDto> memoResDtos;
        private Integer foodsPrice;
        private Double commission;
        private Integer commissionPrice;
        private Integer totalPrice;
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
        private Integer prepaidPrice;
        private Integer price;
        private String managerName;
        private String phone;
        private String paycheckStatus;
        private Boolean hasRequest;
        private String excelFile;
        private String pdfFile;

        @Builder
        public CorporationResponse(BigInteger id, Integer year, Integer month, String corporationName, Integer prepaidPrice, Integer price, String managerName, String phone, String paycheckStatus, Boolean hasRequest, String excelFile, String pdfFile) {
            this.id = id;
            this.year = year;
            this.month = month;
            this.corporationName = corporationName;
            this.prepaidPrice = prepaidPrice;
            this.price = price;
            this.managerName = managerName;
            this.phone = phone;
            this.paycheckStatus = paycheckStatus;
            this.hasRequest = hasRequest;
            this.excelFile = excelFile;
            this.pdfFile = pdfFile;
        }
    }

    @Getter
    public static class StatusList {
        private String status;
        private Integer count;

        public StatusList(String status, Integer count) {
            this.status = status;
            this.count = count;
        }
    }

    @Getter
    @Setter
    public static class CorporationMain {
        private List<CorporationResponse> corporationResponses;
        private List<StatusList> statusLists;

        public CorporationMain(List<CorporationResponse> corporationResponses, List<StatusList> statusLists) {
            this.corporationResponses = corporationResponses;
            this.statusLists = statusLists;
        }
    }

    @Getter
    @Setter
    public static class CorporationOrder {
        private List<CorporationOrderItem> corporationOrderItems;
        private CorporationInfo corporationInfo;
    }

    @Getter
    @Setter
    public static class CorporationInfo {
        private String name;
        private String period;
        private Integer morningCount;
        private Integer lunchCount;
        private Integer dinnerCount;
        private Integer totalPrice;

        @Builder
        public CorporationInfo(String name, String period, Integer totalPrice, Integer morningCount, Integer lunchCount, Integer dinnerCount) {
            this.name = name;
            this.period = period;
            this.totalPrice = totalPrice;
            this.morningCount = morningCount;
            this.lunchCount = lunchCount;
            this.dinnerCount = dinnerCount;
        }
    }

    @Getter
    @Setter
    public static class paymentCategory {
        private String paymentCategory;
        private Integer count;
    }

    @Getter
    @Setter
    public static class CorporationOrderItem {
        private String serviceDate;
        private String diningType;
        private String makers;
        private String food;
        private Integer count;
        private Integer supportPrice;
        private String user;
        private String email;

        @Builder
        public CorporationOrderItem(OrderItemDailyFood orderItemDailyFood, BigDecimal supportPrice) {
            this.serviceDate = DateUtils.format(orderItemDailyFood.getDailyFood().getServiceDate());
            this.diningType = orderItemDailyFood.getDailyFood().getDiningType().getDiningType();
            this.makers = orderItemDailyFood.getDailyFood().getFood().getMakers().getName();
            this.food = orderItemDailyFood.getName();
            this.supportPrice = supportPrice.intValue();
            this.user = orderItemDailyFood.getOrder().getUser().getName();
            this.email = orderItemDailyFood.getOrder().getUser().getEmail();

            // 음식을 여러개 주문 하였을 경우
            // FIXME: 메드트로닉은 다른 로직
            for (int i = 1; i <= orderItemDailyFood.getCount(); i++) {
                BigDecimal discountedPrice = orderItemDailyFood.getDiscountedPrice();
                if (discountedPrice.multiply(BigDecimal.valueOf(i)).compareTo(supportPrice) >= 0) {
                    this.count = i;
                }
            }
        }
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
    public static class MemoDto {
        private String memo;
    }

    @Getter
    @Setter
    public static class MemoResDto {
        private String writer;
        private String memo;
        private String createdDateTime;

        public MemoResDto(String writer, String memo, String createdDateTime) {
            this.writer = writer;
            this.memo = memo;
            this.createdDateTime = createdDateTime;
        }
    }

    @Getter
    @Setter
    public static class PaycheckCategory {
        private String category;
        private Integer price;
        private Integer count;
        private Integer days;
        private Integer totalPrice;
    }

    @Getter
    @Setter
    public static class Invoice {
        private CorporationResponse corporationResponse;
        private TransactionInfoDefault transactionInfoDefault;
        private List<PaycheckCategory> prepaidPaycheck;
        private List<PaycheckCategory> paycheck;
        private List<PaycheckAddDto> paycheckAdds;
        private List<MemoResDto> memoResDtos;
        private Integer prepaidTotalPrice;
        private Integer totalPrice;
        private Integer vatTotalPrice;
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
        private String excelFile;
        private String pdfFile;

        @Builder

        public MakersPaycheckInfo(String year, String month, String makers, String status, String depositHolder, String bankName, String bankAccount, String excelFile, String pdfFile) {
            this.year = year;
            this.month = month;
            this.makers = makers;
            this.status = status;
            this.depositHolder = depositHolder;
            this.bankName = bankName;
            this.bankAccount = bankAccount;
            this.excelFile = excelFile;
            this.pdfFile = pdfFile;
        }
    }
}
