package co.dalicious.domain.paycheck.service.Impl;

import co.dalicious.domain.file.service.ImageService;
import co.dalicious.domain.paycheck.dto.TransactionInfoDefault;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import co.dalicious.domain.paycheck.entity.PaycheckDailyFood;
import co.dalicious.domain.paycheck.service.ExcelService;
import co.dalicious.domain.paycheck.service.PaycheckService;

import co.dalicious.domain.paycheck.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {
    private final PaycheckService paycheckService;
    private final ImageService imageService;
    private final PdfService pdfService;

    @Override
    public void createMakersPaycheckExcel(MakersPaycheck makersPaycheck) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("MakersPaycheck");

        sheet.setColumnWidth(0, 2 * 256);

        String fileName = "C:\\Users\\minji\\Downloads\\" + makersPaycheck.getYearMonth().getYear() +
                ((makersPaycheck.getYearMonth().getMonthValue() < 10) ? "0" + String.valueOf(makersPaycheck.getYearMonth().getMonthValue()) : String.valueOf(makersPaycheck.getYearMonth().getMonthValue())) +
                "_" + makersPaycheck.getMakers().getName() + ".xlsx";

        String fileName2 = "C:\\Users\\minji\\Downloads\\" + makersPaycheck.getYearMonth().getYear() +
                ((makersPaycheck.getYearMonth().getMonthValue() < 10) ? "0" + String.valueOf(makersPaycheck.getYearMonth().getMonthValue()) : String.valueOf(makersPaycheck.getYearMonth().getMonthValue())) +
                "_" + makersPaycheck.getMakers().getName() + ".pdf";

        // Create header rows
        createHeaderRows(workbook, sheet);

        // Write daily foods data
        int currentRow = 13;
        List<PaycheckDailyFood> dailyFoods = makersPaycheck.getPaycheckDailyFoods();
        for (PaycheckDailyFood dailyFood : dailyFoods) {
            Row row = sheet.createRow(currentRow);
            writeDailyFood(workbook, row, dailyFood);
            sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 2, 4));
            currentRow++;
        }

        // Write total row
        Integer footerRowNumber = writeTotalRow(sheet, currentRow + 3, dailyFoods);
        createFooterRows(sheet, footerRowNumber);

        // Adjust column widths
        for (int i = 0; i < 11; i++) {
            sheet.autoSizeColumn(i);
        }

        // Save the file locally
        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfService.excelToPdf(workbook, fileName2);
    }


    @Override
    public void addImageToWorkbook(Workbook workbook, Sheet sheet, byte[] imageBytes, int col1, int row1, double scaleX, double scaleY) {
        int pictureIndex = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);

        CreationHelper helper = workbook.getCreationHelper();
        Drawing<?> drawing = sheet.createDrawingPatriarch();

        ClientAnchor anchor = helper.createClientAnchor();
        anchor.setCol1(col1);
        anchor.setRow1(row1);
        anchor.setDx1(0); // Horizontal offset in 1024ths of a column width
        anchor.setDy1(0); // Vertical offset in 1024ths of a row height

        Picture picture = drawing.createPicture(anchor, pictureIndex);
        picture.resize(scaleX, scaleY);
    }


    private void createHeaderRows(Workbook workbook, Sheet sheet) {
        // 거래명세서 제목
        Row row1 = sheet.createRow(1);
        Cell cell1_1 = row1.createCell(1);
        cell1_1.setCellValue("거래명세서");
        cell1_1.setCellStyle(titleStyle(workbook));

        Cell cell1_6 = row1.createCell(6);
        cell1_6.setCellValue("2023-03");
        cell1_6.setCellStyle(center(workbook));
        sheet.addMergedRegion(new CellRangeAddress(1, 3, 1, 2));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 6, 7));

        // 수신인
        Row row4 = sheet.createRow(4);
        CellStyle borderStyle = workbook.createCellStyle();
        borderStyle.setBorderBottom(BorderStyle.THIN);

        for (int i = 1; i <= 7; i++) {
            row4.createCell(i);
            row4.getCell(i).setCellStyle(borderStyle);
            if (i == 1) {
                row4.getCell(i).setCellValue("수신");
            }
            // TODO: 수정필요
            if (i == 2) {
                row4.getCell(i).setCellValue("모모유부 역삼점");
            }
        }

        // 공급자(달리셔스) 정보
        TransactionInfoDefault transactionInfoDefault = paycheckService.getTransactionInfoDefault();
        Row row6 = sheet.createRow(6);
        Cell cell6_1 = row6.createCell(1);
        cell6_1.setCellStyle(right(workbook));
        cell6_1.setCellValue("사업자번호");

        Cell cell6_2 = row6.createCell(2);
        cell6_2.setCellValue(transactionInfoDefault.getBusinessNumber());

        Cell cell6_4 = row6.createCell(4);
        cell6_4.setCellValue("주소");

        Cell cell6_5 = row6.createCell(5);
        cell6_5.setCellValue(transactionInfoDefault.getAddress1());
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 5, 6));

        Row row7 = sheet.createRow(7);
        Cell cell7_1 = row7.createCell(1);
        cell7_1.setCellStyle(right(workbook));
        cell7_1.setCellValue("상호");

        Cell cell7_2 = row7.createCell(2);
        cell7_2.setCellValue(transactionInfoDefault.getCorporationName());

        Cell cell7_5 = row7.createCell(5);
        cell7_5.setCellValue(transactionInfoDefault.getAddress2());
        sheet.addMergedRegion(new CellRangeAddress(7, 7, 5, 6));

        // 도장 추가
        addImageToWorkbook(workbook, sheet, getStamp(), 3, 7, 0.8, 2);

        Row row8 = sheet.createRow(8);
        Cell cell8_1 = row8.createCell(1);
        cell8_1.setCellStyle(right(workbook));
        cell8_1.setCellValue("대표자");

        Cell cell8_2 = row8.createCell(2);
        cell8_2.setCellValue(transactionInfoDefault.getRepresentative());

        Row row9 = sheet.createRow(9);
        Cell cell9_1 = row9.createCell(1);
        cell9_1.setCellStyle(right(workbook));
        cell9_1.setCellValue("전화");

        Cell cell9_2 = row9.createCell(2);
        cell9_2.setCellValue(transactionInfoDefault.getPhone());

        Cell cell9_4 = row9.createCell(4);
        cell9_4.setCellValue("업태");

        Cell cell9_5 = row9.createCell(5);
        cell9_5.setCellValue(transactionInfoDefault.getBusiness());

        Row row10 = sheet.createRow(10);
        Cell cell10_1 = row10.createCell(1);
        cell10_1.setCellStyle(right(workbook));
        cell10_1.setCellValue("팩스");

        Cell cell10_2 = row10.createCell(2);
        cell10_2.setCellValue(transactionInfoDefault.getFaxNumber());

        Cell cell10_4 = row10.createCell(4);
        cell10_4.setCellValue("종목");

        Cell cell10_5 = row10.createCell(5);
        cell10_5.setCellValue(transactionInfoDefault.getBusinessForm());
        sheet.addMergedRegion(new CellRangeAddress(10, 10, 5, 7));

        // 거래 내역 헤더
        Row row12 = sheet.createRow(12);
        String[] headers = {"일자", "메뉴명", "금액", "수량", "금액(vat포함)"};
        for (int i = 1; i <= headers.length; i++) {
            Cell cell13 = null;
            if (i < 2) {
                cell13 = row12.createCell(i);
                cell13.setCellValue(headers[i - 1]);
            }
            if (i == 2) {
                cell13 = row12.createCell(i);
                cell13.setCellValue(headers[i - 1]);
                sheet.addMergedRegion(new CellRangeAddress(12, 12, 2, 4));
            }
            if (i > 2) {
                cell13 = row12.createCell(i + 2);
                cell13.setCellValue(headers[i - 1]);
            }
            cell13.setCellStyle(dataHeader(workbook));
        }
    }

    private static void writeDailyFood(Workbook workBook, Row row, PaycheckDailyFood dailyFood) {
        CellStyle dataCellStyle = center(workBook);
        CellStyle priceCellStyle = priceStyle(workBook);

        Cell cell1 = row.createCell(1);
        cell1.setCellValue(dailyFood.getServiceDate().toString());
        cell1.setCellStyle(dataCellStyle);

        Cell cell2 = row.createCell(2);
        cell2.setCellValue(dailyFood.getName());
        cell2.setCellStyle(dataCellStyle);

        Cell cell3 = row.createCell(5);
        cell3.setCellValue(dailyFood.getSupplyPrice() == null ? null : dailyFood.getSupplyPrice().intValue());
        cell3.setCellStyle(priceCellStyle);

        Cell cell4 = row.createCell(6);
        cell4.setCellValue(dailyFood.getCount());
        cell4.setCellStyle(dataCellStyle);

        Cell cell5 = row.createCell(7);
        cell5.setCellValue(dailyFood.getTotalPrice() == null ? null : dailyFood.getTotalPrice().intValue());
        cell5.setCellStyle(priceCellStyle);
    }

    private static Integer writeTotalRow(Sheet sheet, Integer startRow, List<PaycheckDailyFood> dailyFoods) {
        // 총액 셀 추가
        Row totalPriceRow = sheet.createRow(startRow);
        Integer total = dailyFoods.stream()
                .map(df -> df.getSupplyPrice().multiply(BigDecimal.valueOf(df.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add).intValue();


        Cell cell1 = totalPriceRow.createCell(5);
        cell1.setCellValue("총액");
        cell1.setCellStyle(boldCenter(sheet.getWorkbook()));

        Cell cell1_2 = totalPriceRow.createCell(6);
        cell1_2.setCellValue(total);
        cell1_2.setCellStyle(boldPriceStyle(sheet.getWorkbook()));

        // 수수료 셀 추가
        Row chargeRow = sheet.createRow(startRow + 1);
        Cell cell2 = chargeRow.createCell(5);
        cell2.setCellValue("수수료");
        cell2.setCellStyle(boldCenter(sheet.getWorkbook()));

        // TODO: 수정필요
        Cell cell2_2 = chargeRow.createCell(6);
        cell2_2.setCellValue(0);
        cell2_2.setCellStyle(boldPriceStyle(sheet.getWorkbook()));

        // 결제 금액 셀 추가
        Row payPriceRow = sheet.createRow(startRow + 2);
        Cell cell3 = payPriceRow.createCell(5);
        cell3.setCellValue("Total");
        cell3.setCellStyle(boldCenter(sheet.getWorkbook()));

        // TODO: 수정필요
        Cell cell3_2 = payPriceRow.createCell(6);
        cell3_2.setCellValue(total);
        cell3_2.setCellStyle(priceStyle(sheet.getWorkbook()));

        return startRow + 3;
    }

    private void createFooterRows(Sheet sheet, Integer footerRowNumber) {
        Row footerRow = sheet.createRow(footerRowNumber);
        CellStyle borderStyle = sheet.getWorkbook().createCellStyle();
        borderStyle.setBorderTop(BorderStyle.THIN);

        for (int i = 1; i <= 7; i++) {
            footerRow.createCell(i);
            footerRow.getCell(i).setCellStyle(borderStyle);
            if (i == 1) {
                footerRow.getCell(i).setCellValue("위와 같이 명세서 제출합니다.");
            }
        }
        sheet.addMergedRegion(new CellRangeAddress(footerRowNumber, footerRowNumber, 1, 7));
        addImageToWorkbook(sheet.getWorkbook(), sheet, getLogo(), 5, footerRowNumber, 3, 3);
    }

    private static CellStyle titleStyle(Workbook workbook) {
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 18);
        font.setFontName("Calibri");
        titleStyle.setFont(font);
        return titleStyle;
    }

    private static CellStyle center(Workbook workbook) {
        CellStyle center = workbook.createCellStyle();
        center.setAlignment(HorizontalAlignment.CENTER);
        center.setVerticalAlignment(VerticalAlignment.CENTER);
        return center;
    }

    private static CellStyle boldCenter(Workbook workbook) {
        CellStyle center = workbook.createCellStyle();
        center.setAlignment(HorizontalAlignment.CENTER);
        center.setVerticalAlignment(VerticalAlignment.CENTER);

        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        center.setFont(boldFont);
        return center;
    }

    private static CellStyle priceStyle(Workbook workbook) {
        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setAlignment(HorizontalAlignment.CENTER);
        currencyStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        DataFormat format = workbook.createDataFormat();
        currencyStyle.setDataFormat(format.getFormat("\\₩ #,##0"));
        return currencyStyle;
    }

    private static CellStyle boldPriceStyle(Workbook workbook) {
        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setAlignment(HorizontalAlignment.CENTER);
        currencyStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        DataFormat format = workbook.createDataFormat();
        currencyStyle.setDataFormat(format.getFormat("\\₩ #,##0"));

        return currencyStyle;
    }

    private static CellStyle right(Workbook workbook) {
        CellStyle center = workbook.createCellStyle();
        center.setAlignment(HorizontalAlignment.RIGHT);
        center.setVerticalAlignment(VerticalAlignment.CENTER);
        return center;
    }

    private static CellStyle dataHeader(Workbook workbook) {
        XSSFCellStyle dataHeader = (XSSFCellStyle) workbook.createCellStyle();
        dataHeader.setAlignment(HorizontalAlignment.CENTER);
        dataHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        byte[] customColor = new byte[]{(byte) 230, (byte) 230, (byte) 230};
        XSSFColor xssfColor = new XSSFColor(customColor, new DefaultIndexedColorMap());
        dataHeader.setFillForegroundColor(xssfColor);
        dataHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return dataHeader;
    }


    private byte[] getStamp() {
        return imageService.downloadImageFromS3("util/stamp.png");
    }

    private byte[] getLogo() {
        return imageService.downloadImageFromS3("util/logo.png");
    }
}
