package co.kurrant.app.admin_api.dto.client;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Builder
public class CorporationListDto {
    private BigInteger id;
    private String code;
    private String name;
    private Integer zipCode;
    private String address1;
    private String address2;
    private String location;
    private List<Integer> diningTypes;
    private List<String> serviceDays;
    private String managerName;
    private String managerPhone;
    private Boolean isMembershipSupport;
    private Integer employeeCount;
    private Boolean isSetting;
    private Boolean isGarbage;
    private Boolean isHotStorage;
    private String createdDateTime;
    private String updatedDateTime;
}
