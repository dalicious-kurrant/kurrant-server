package co.dalicious.domain.client.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class GroupExcelRequestDto {
    private BigInteger id;
    private Integer groupType;
    private String code;
    private String name;
    private String zipCode;
    private String address1;
    private String address2;
    private String location;
    private List<String> diningTypes;
    @NotNull
    private String serviceDays;
    private BigInteger managerId;
    private String managerName;
    private String managerPhone;
    private String isMembershipSupport;
    private Integer employeeCount;
    private String isSetting;
    private String isGarbage;
    private String isHotStorage;
    private Integer morningSupportPrice;
    private Integer lunchSupportPrice;
    private Integer dinnerSupportPrice;
    private Integer minimumSpend;
    private Integer maximumSpend;
}
