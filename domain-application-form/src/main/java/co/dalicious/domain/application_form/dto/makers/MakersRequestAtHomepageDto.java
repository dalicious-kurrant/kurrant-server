package co.dalicious.domain.application_form.dto.makers;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MakersRequestAtHomepageDto {
    @NotNull(message = "이름은 필수 값입니다.")
    private String name;
    @NotNull(message = "상호명은 필수 값입니다.")
    private String makersName;
    @NotNull(message = "지역은 필수 값입니다.")
    private String address;
    @NotNull(message = "번호는 필수 값입니다.")
    private String phone;
    private String memo;
    @NotNull(message = "메인 상품은는 필수 값입니다.")
    private String mainProduct;
}
