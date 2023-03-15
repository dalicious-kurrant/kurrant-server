package co.dalicious.domain.food.dto;

import co.dalicious.domain.food.entity.Makers;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Builder
public class MakersInfo {
    private BigInteger makersId;
    private String makersName;

    public static MakersInfo create(Makers makers) {
        return MakersInfo.builder()
                .makersId(makers.getId())
                .makersName(makers.getName())
                .build();
    }
}
