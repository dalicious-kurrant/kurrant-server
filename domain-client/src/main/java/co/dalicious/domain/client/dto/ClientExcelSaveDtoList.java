package co.dalicious.domain.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "엑셀 저장 요청 DTO")
public class ClientExcelSaveDtoList {

   private List<ClientExcelSaveDto> saveList;
}
