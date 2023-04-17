package co.kurrant.app.client_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class MemberIdListDto {

    List<BigInteger> userIdList;
}
