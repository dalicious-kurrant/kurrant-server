package co.dalicious.domain.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReviewListDto {
    private List<String> imageLocation;
    private String content;
    private Integer satisfaction;
    private String createDate;
    private String updateDate;
    private Boolean forMakers;
    private String makersName;
    private String itemName;

    @Builder
    public ReviewListDto(List<String> imageLocation, String content, Integer satisfaction,String createDate, String updateDate,Boolean forMakers, String makersName,String itemName) {
        this.imageLocation = imageLocation;
        this.content = content;
        this.satisfaction = satisfaction;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.forMakers = forMakers;
        this.makersName = makersName;
        this.itemName = itemName;
    }
}
