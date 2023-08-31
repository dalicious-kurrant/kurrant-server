package co.dalicious.domain.application_form.dto.corporation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CorporationRequestAtHomepageDto {
    private String name;
    private String address;
    private String phone;
    private String memo;
}
