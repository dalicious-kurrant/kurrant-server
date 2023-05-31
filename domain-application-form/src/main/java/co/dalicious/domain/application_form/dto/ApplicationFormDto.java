package co.dalicious.domain.application_form.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "스팟 정보 리스트 응답 DTO")
public class ApplicationFormDto {
    private Integer clientType;
    private BigInteger id;
    private String name;
    private String address;
    private Boolean isExist;

    @Builder
    public ApplicationFormDto(Integer clientType, BigInteger id, String name, String address, Boolean isExist) {
        this.clientType = clientType;
        this.id = id;
        this.name = name;
        this.address = address;
        this.isExist = isExist;
    }
}
