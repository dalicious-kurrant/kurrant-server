package co.dalicious.domain.application_form.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class ApplicationFormDto {
    private Integer clientType;
    private BigInteger id;
    private String name;
    private String date;

    @Builder
    public ApplicationFormDto(Integer clientType, BigInteger id, String name, String date) {
        this.clientType = clientType;
        this.id = id;
        this.name = name;
        this.date = date;
    }
}
