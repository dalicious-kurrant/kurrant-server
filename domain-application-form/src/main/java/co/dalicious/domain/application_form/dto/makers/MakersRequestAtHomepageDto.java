package co.dalicious.domain.application_form.dto.makers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MakersRequestAtHomepageDto {
    private String name;
    private String makersName;
    private String address;
    private String phone;
    private String memo;
    private String mainProduct;
    private String progressStatus;
}
