package co.dalicious.domain.paycheck.dto;

import co.dalicious.domain.file.dto.ImageResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter

public class ExcelPdfDto {
    private ImageResponseDto pdfDto;
    private ImageResponseDto excelDto;

    public ExcelPdfDto(ImageResponseDto pdfDto, ImageResponseDto excelDto) {
        this.pdfDto = pdfDto;
        this.excelDto = excelDto;
    }
}

