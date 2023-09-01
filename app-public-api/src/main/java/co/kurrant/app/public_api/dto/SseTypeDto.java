package co.kurrant.app.public_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SseTypeDto {
    private Integer type;
    private List<String> ids;
}
