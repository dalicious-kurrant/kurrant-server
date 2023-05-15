package co.dalicious.domain.client.dto.mySpotZone.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityInfo {
    private String cityName;

    public CityInfo(String cityName) {
        this.cityName = cityName;
    }
}
