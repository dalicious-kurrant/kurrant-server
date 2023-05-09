package co.dalicious.domain.paycheck.service;

import co.dalicious.domain.file.dto.ImageResponseDto;
import co.dalicious.domain.paycheck.dto.ExcelPdfDto;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.entity.CorporationPaycheck;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public interface ExcelService {
    ExcelPdfDto createMakersPaycheckExcel(MakersPaycheck makersPaycheck);
    void addImageToWorkbook(Workbook workbook, Sheet sheet, byte[] imageBytes, int col1, int row1, double scaleX, double scaleY);
    ExcelPdfDto createCorporationPaycheckExcel(CorporationPaycheck corporationPaycheck, PaycheckDto.CorporationOrder corporationOrder);
}
