package co.dalicious.domain.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "메이커스 저장 요청 DTO 목록")
public class SaveMakersRequestDtoList {
    List<SaveMakersRequestDto> saveMakersRequestDto;
}
