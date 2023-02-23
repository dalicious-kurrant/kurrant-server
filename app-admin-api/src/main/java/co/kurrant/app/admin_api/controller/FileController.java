package co.kurrant.app.admin_api.controller;


import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.file.service.ImageService;
import co.kurrant.app.admin_api.dto.ExcelExample;
import co.kurrant.app.admin_api.service.ExcelService;
import co.kurrant.app.admin_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.Tika;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "File")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/admins/files")
@RestController
@CrossOrigin(origins="*", allowedHeaders = "*")
public class FileController {
    private final ExcelService excelService;
    private final ImageService imageService;


    @Operation(summary = "이미지 업로드 경로 요청", description = "이미지 업로드 경로 요청한다.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/images")
    public ResponseMessage uploadImage(List<MultipartFile> multipartFiles) throws IOException {
        return ResponseMessage.builder()
                .data(imageService.upload(multipartFiles, "food"))
                .message("S3 이미지 업로드에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "엑셀 파일 작업 연습", description = "엑셀 파일 읽고 쓰는 연슴용")
    @PostMapping("/excel")
    public List<ExcelExample> excelPractice(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        List<ExcelExample> dataList = new ArrayList<>();

        try (InputStream is = file.getInputStream();) {
            Tika tika = new Tika();
            String mimeType = tika.detect(is);
            if (isAllowedMIMEType(mimeType)) {
                Workbook workbook = new XSSFWorkbook(file.getInputStream());

                Sheet worksheet = workbook.getSheetAt(0);

                String atchFileId = null;

                for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) { // 1번째 행부터 끝까지
                    Row row = worksheet.getRow(i);

                    ExcelExample data = new ExcelExample();
                    data.setUserId((int) row.getCell(0).getNumericCellValue());
                    data.setName(row.getCell(1).getStringCellValue());
                    data.setPhone(row.getCell(2).getStringCellValue());
                    data.setEmail(row.getCell(3).getStringCellValue());
                    data.setCorporationName(row.getCell(4).getStringCellValue());

                    dataList.add(data);
                }

                model.addAttribute("list", dataList);
            } else {
                throw new ApiException(ExceptionEnum.NOT_FOUND);
            }
        } catch (Exception e) {
            throw new ApiException(ExceptionEnum.BAD_REQUEST);
        }
        return dataList;
    }

    private boolean isAllowedMIMEType(String mimeType) {
        return mimeType.equals("application/x-tika-ooxml");
    }
  @Operation(summary = "전체 조회 엑셀 파일 불러오기", description = "전체 조회 엑셀 파일의 데이터를 불러옵니다.")
  @PostMapping("/food/all")
  public ResponseMessage allFoodExcel(Authentication authentication, @RequestParam("file") MultipartFile file) throws IOException {
    UserUtil.securityUser(authentication);
    return ResponseMessage.builder()
            .message("엑셀 불러오기를 완료했습닌다.")
            .data(excelService.allFoodExcel(file))
            .build();

  }

  @Operation(summary = "엑셀 파일 불러오기", description = "엑셀 파일의 데이터를 불러옵니다.")
  @PostMapping("/food/makers")
  public ResponseMessage makersFoodExcel(Authentication authentication, @RequestParam("file") MultipartFile file) throws IOException {
    UserUtil.securityUser(authentication);
    return ResponseMessage.builder()
            .message("엑셀 불러오기를 완료했습닌다.")
            .data(excelService.makersFoodExcel(file))
            .build();

  }

}

