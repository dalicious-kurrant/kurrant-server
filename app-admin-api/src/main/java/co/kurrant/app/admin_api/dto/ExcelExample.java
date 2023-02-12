package co.kurrant.app.admin_api.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
public class ExcelExample {
    private Integer userId;
    private String name;
    private String phone;
    private String email;
    private String corporationName;
}
