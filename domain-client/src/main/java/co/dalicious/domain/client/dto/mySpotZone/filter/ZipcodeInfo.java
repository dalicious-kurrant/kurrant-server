package co.dalicious.domain.client.dto.mySpotZone.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZipcodeInfo {
    private String zipcode;

    public ZipcodeInfo(String zipcode) {
        this.zipcode = zipcode;
    }
}
