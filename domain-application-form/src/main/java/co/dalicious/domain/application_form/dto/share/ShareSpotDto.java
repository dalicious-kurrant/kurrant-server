package co.dalicious.domain.application_form.dto.share;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

public class ShareSpotDto {
    @Getter
    @Setter
    public static class Request {
        private BigInteger groupId;
        private CreateAddressRequestDto address;
        private String deliveryTime;
        private Boolean entranceOption;
        private String memo;
    }

    @Getter
    @Setter
    public static class Response {
        private BigInteger id;
        private String shareSpotRequestType;
        private BigInteger userId;
        private BigInteger groupId;
        private String address1;
        private String address2;
        private String zipCode;
        private String deliveryTime;
        private Boolean entranceOption;
        private String memo;
        private String createdDate;
    }

    @Getter
    @Setter
    public static class AdminRequest {
        private Integer shareSpotRequestType;
        private BigInteger userId;
        private BigInteger groupId;
        private CreateAddressRequestDto address;
        private String deliveryTime;
        private Boolean entranceOption;
        private String memo;
    }
}
