package co.dalicious.domain.application_form.dto.corporation;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CorporationRequestAtHomepageDto {
    @NotNull
    private String name;
    @NotNull
    private String address;
    @NotNull
    private String phone;
    private String memo;
}
