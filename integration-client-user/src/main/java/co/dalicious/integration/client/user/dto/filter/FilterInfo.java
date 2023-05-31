package co.dalicious.integration.client.user.dto.filter;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class FilterInfo {
    private BigInteger id;
    private String name;
}
