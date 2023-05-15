package co.dalicious.domain.client.dto.mySpotZone.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VillageInfo {
    private String village;

    public VillageInfo(String village) {
        this.village = village;
    }
}
