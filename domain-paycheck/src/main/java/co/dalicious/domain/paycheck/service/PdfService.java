package co.dalicious.domain.paycheck.service;

import org.apache.poi.ss.usermodel.Workbook;

public interface PdfService {
    void excelToPdf(Workbook workbook, String pdfFilePath);
}
