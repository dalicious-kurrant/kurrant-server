package co.dalicious.domain.banner.entity.dto;

import lombok.Getter;

public class AdvertisementDto {
    @Getter
    public static class Request {
        private String startDate;
        private String endDate;
        private Integer type;
        private Integer section;
    }
}
