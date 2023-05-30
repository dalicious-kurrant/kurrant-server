package co.dalicious.domain.client.dto.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterRequest {

    private String city;
    private String county;
    private List<String> villages;

}
