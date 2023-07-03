package co.dalicious.domain.client.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupCountDto {
    private Integer privateCount;
    private Integer mySpotCount;
    private Integer shareSpotCount;
    private List<SpotListResponseDto> spotListResponseDtoList;
}
