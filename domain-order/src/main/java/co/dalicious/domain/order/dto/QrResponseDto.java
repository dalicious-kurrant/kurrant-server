package co.dalicious.domain.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
public class QrResponseDto {
    private String createdDateTime;
    private String expiredDateTime;
    private List<Item> items;

    @Builder
    public QrResponseDto(String createdDateTime, String expiredDateTime, List<Item> items) {
        this.createdDateTime = createdDateTime;
        this.expiredDateTime = expiredDateTime;
        this.items = items;
    }

    @Getter
    @Setter
    public static class Item {
        private BigInteger id;
        private String image;
        private String makersName;
        private String foodName;
        private Integer count;
        private Integer orderStatus;
    }
}
