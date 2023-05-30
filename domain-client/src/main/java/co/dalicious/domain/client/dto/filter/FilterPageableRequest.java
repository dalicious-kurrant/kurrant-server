package co.dalicious.domain.client.dto.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterPageableRequest {

    private String name;
    private String city;
    private String county;
    private List<String> villages;
    private List<String> zipcode;
    private Integer status;
    private Integer limit;
    private Integer size;
}
