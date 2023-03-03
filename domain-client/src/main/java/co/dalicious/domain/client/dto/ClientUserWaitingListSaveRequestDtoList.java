package co.dalicious.domain.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClientUserWaitingListSaveRequestDtoList {
    @Schema(description = "기업코드")
    private String code;
    @Schema(description = "고객사 유저정보 저장 목록")
    private List<ClientUserWaitingListSaveRequestDto> saveUserList;
}
