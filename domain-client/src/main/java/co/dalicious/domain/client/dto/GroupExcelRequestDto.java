package co.dalicious.domain.client.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class GroupExcelRequestDto {
    private BigInteger id;
    private String code;
    private String name;
    private Integer zipCode;
    private String address1;
    private String address2;
    private String location;
    private List<String> diningTypes;
    private String serviceDays;
    private BigInteger managerId;
    private String managerName;
    private String managerPhone;
    private String isMembershipSupport;
    private Integer employeeCount;
    private String isSetting;
    private String isGarbage;
    private String isHotStorage;
    private String morningSupportPrice;
    private String lunchSupportPrice;
    private String dinnerSupportPrice;
}
