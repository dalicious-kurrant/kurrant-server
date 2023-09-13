package co.kurrant.app.admin_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class UpdateStatusAndIdListDto {
    private List<BigInteger> ids;
    private Integer currentStatus;
    private Integer updateStatus;
}
