package co.kurrant.app.public_api.dto.client;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApplicationFormDto {
    private Integer clientType;
    private Long id;
    private String name;
    private String date;

    @Builder
    public ApplicationFormDto(Integer clientType, Long id, String name, String date) {
        this.clientType = clientType;
        this.id = id;
        this.name = name;
        this.date = date;
    }
}
