package co.dalicious.domain.logs.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class AdminLogsDto {
    private BigInteger id;
    private Integer logType;
    private Integer controllerType;
    private String baseUrl;
    private String endPoint;
    private String entityName;
    private String userCode;
    private List<String> logs;
    private String createdDateTime;
}
