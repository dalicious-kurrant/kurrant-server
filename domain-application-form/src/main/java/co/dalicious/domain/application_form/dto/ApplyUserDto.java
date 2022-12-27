package co.dalicious.domain.application_form.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "스팟 개설 신청 담당자 정보 DTO")
@Getter
@NoArgsConstructor
public class ApplyUserDto {
    private String name;
    private String phone;
    private String email;

    @Builder
    public ApplyUserDto(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }
}
