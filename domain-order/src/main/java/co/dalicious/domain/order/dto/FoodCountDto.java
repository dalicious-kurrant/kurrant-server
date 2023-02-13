package co.dalicious.domain.order.dto;

import lombok.Getter;

@Getter
public class FoodCountDto {
    private Integer remainCount;
    private Boolean isFollowingMakersCapacity;

    public FoodCountDto(Integer remainCount, Boolean isFollowingMakersCapacity) {
        this.remainCount = remainCount;
        this.isFollowingMakersCapacity = isFollowingMakersCapacity;
    }
}
