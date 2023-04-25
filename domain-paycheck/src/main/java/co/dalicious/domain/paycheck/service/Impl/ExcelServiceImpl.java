package co.dalicious.domain.paycheck.service.Impl;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.PaycheckCategory;
import co.dalicious.domain.file.dto.ImageResponseDto;
import co.dalicious.domain.file.service.ImageService;
import co.dalicious.domain.paycheck.dto.TransactionInfoDefault;
import co.dalicious.domain.paycheck.entity.*;
import co.dalicious.domain.paycheck.service.ExcelService;

import co.dalicious.system.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {
    private final ImageService imageService;

    @Override
    @Transactional
    public ImageResponseDto createMakersPaycheckExcel(MakersPaycheck makersPaycheck) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("MakersPaycheck");

        sheet.setColumnWidth(0, 2 * 256);
        String dirName = "paycheck/makers/" + makersPaycheck.getMakers().getId().toString() + "/" + makersPaycheck.getYearAndMonthString() + "/" + makersPaycheck.getMakers().getId().toString();

        String fileName = makersPaycheck.getFileName() +
                "_" + makersPaycheck.getMakers().getName() + ".xlsx";

        String fileName2 = "C:\\Users\\minji\\Downloads\\" + makersPaycheck.getYearMonth().getYear() +
                ((makersPaycheck.getYearMonth().getMonthValue() < 10) ? "0" + String.valueOf(makersPaycheck.getYearMonth().getMonthValue()) : String.valueOf(makersPaycheck.getYearMonth().getMonthValue())) +
                "_" + makersPaycheck.getMakers().getName() + ".pdf";

        // Create header rows
        createHeaderRows(workbook, sheet, makersPaycheck.getMakers().getName(), makersPaycheck.getYearMonth());
        createDailyFoodHeader(workbook, sheet);

        // Write daily foods data
        int currentRow = 13;
        List<PaycheckDailyFood> dailyFoods = makersPaycheck.getPaycheckDailyFoods();
        for (PaycheckDailyFood dailyFood : dailyFoods) {
            Row row = sheet.createRow(currentRow);
            writeDailyFood(workbook, row, dailyFood);
            sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 2, 4));
            currentRow++;
        }

        // 추가 요청 헤더 생성
        List<PaycheckAdd> paycheckAdds = makersPaycheck.getPaycheckAdds();
        if (!paycheckAdds.isEmpty()) {
            Row row = sheet.createRow(++currentRow);
            createDailyFoodAddHeader(workbook, sheet, row);
            currentRow++;
            for (PaycheckAdd paycheckAdd : paycheckAdds) {
                writeDailyFoodAdd(workbook, sheet, currentRow, paycheckAdd);
                sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 2, 6));
                currentRow++;
            }
        }


        // 총 금액 row 추가
        Integer footerRowNumber = writeMakersTotalRow(sheet, currentRow + 3, makersPaycheck);
        createFooterRows(sheet, footerRowNumber);

        // Adjust column widths
        for (int i = 0; i < 11; i++) {
            sheet.autoSizeColumn(i);
        }

        // Save the file locally
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        // Convert Excel to HTML and then to PDF
//        pdfService.excelToPdf(workbook);
//
//        // Save the PDF file locally
//        try (FileOutputStream pdfFileOutputStream = new FileOutputStream(fileName2)) {
//            pdfOutputStream.writeTo(pdfFileOutputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return imageService.fileUpload(outputStream.toByteArray(), dirName, fileName);
    }

    @Override
    public ImageResponseDto createCorporationPaycheckExcel(CorporationPaycheck corporationPaycheck) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("MakersPaycheck");

        sheet.setColumnWidth(0, 2 * 256);
        String dirName = "paycheck/corporations/" + corporationPaycheck.getCorporation().getId().toString() + "/" + corporationPaycheck.getYearAndMonthString() + "/";

        String fileName = corporationPaycheck.getFileName() +
                "_" + corporationPaycheck.getCorporation().getName() + ".xlsx";


        // 거래명세서 헤더(달리셔스 정보) 생성
        createHeaderRows(workbook, sheet, corporationPaycheck.getCorporation().getName(), corporationPaycheck.getYearMonth());

        // 선금 데이터 생성
        int currentRow = 12;
        ExpectedPaycheck expectedPaycheck = corporationPaycheck.getExpectedPaycheck();
        if (expectedPaycheck != null) {
            currentRow = createHeaderContext(workbook, sheet, currentRow, "선금");
            BigDecimal totalPrice = BigDecimal.ZERO;

            List<PaycheckCategory> paycheckCategories = expectedPaycheck.getPaycheckCategories();
            for (PaycheckCategory paycheckCategory : paycheckCategories) {
                Row row = sheet.createRow(currentRow);
                writePaycheckCategory(workbook, row, paycheckCategory);
                sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 6, 7));
                totalPrice = totalPrice.add(paycheckCategory.getTotalPrice());
                currentRow++;
            }
            currentRow = createCategoryTotalPrice(sheet, currentRow, totalPrice);
            currentRow++;
        }

        List<PaycheckCategory> paycheckCategories = corporationPaycheck.getPaycheckCategories();
        currentRow = createHeaderContext(workbook, sheet, currentRow, "실비");

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (PaycheckCategory paycheckCategory : paycheckCategories) {
            Row row = sheet.createRow(currentRow);
            writePaycheckCategory(workbook, row, paycheckCategory);
            sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 6, 7));
            totalPrice = totalPrice.add(paycheckCategory.getTotalPrice());
            currentRow++;
        }
        currentRow = createCategoryTotalPrice(sheet, currentRow, totalPrice);
        currentRow++;

        // 추가 요청 헤더 생성
        List<PaycheckAdd> paycheckAdds = corporationPaycheck.getPaycheckAdds();
        if (!paycheckAdds.isEmpty()) {
            currentRow = createHeaderTitle(workbook, sheet, currentRow, "추가이슈");
            Row row = sheet.createRow(currentRow);
            createDailyFoodAddHeader(workbook, sheet, row);
            currentRow++;

            BigDecimal addPrice = BigDecimal.ZERO;
            for (PaycheckAdd paycheckAdd : paycheckAdds) {
                writeDailyFoodAdd(workbook, sheet, currentRow, paycheckAdd);
                sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 2, 6));
                addPrice = addPrice.add(paycheckAdd.getPrice());
                currentRow++;
            }
            currentRow = createCategoryTotalPrice_2(sheet, currentRow, addPrice);
        }


        // 총 금액 row 추가
        Integer footerRowNumber = writeCorporationTotalRow(sheet, currentRow + 3, corporationPaycheck);
        createFooterRows(sheet, footerRowNumber);

        // Adjust column widths
        for (int i = 0; i < 11; i++) {
            sheet.autoSizeColumn(i);
        }

        // Save the file locally
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        // Convert Excel to HTML and then to PDF
//        pdfService.excelToPdf(workbook);
//
//        // Save the PDF file locally
//        try (FileOutputStream pdfFileOutputStream = new FileOutputStream(fileName2)) {
//            pdfOutputStream.writeTo(pdfFileOutputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return imageService.fileUpload(outputStream.toByteArray(), dirName, fileName);
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

    // FIXME: 정우님에게 물어보기

    private ByteArrayOutputStream imageToPdf(BufferedImage bufferedImage) {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(new PDRectangle(bufferedImage.getWidth(), bufferedImage.getHeight()));
        document.addPage(page);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            PDImageXObject image = PDImageXObject.createFromByteArray(document, bufferedImageToByteArray(bufferedImage), "excel");
            contentStream.drawImage(image, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
            contentStream.close();
            document.save(byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteArrayOutputStream;
    }

    private byte[] bufferedImageToByteArray(BufferedImage bufferedImage) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }


    private void createHeaderRows(Workbook workbook, Sheet sheet, String name, YearMonth yearMonth) {
        // 거래명세서 제목
        Row row1 = sheet.createRow(1);
        Cell cell1_1 = row1.createCell(1);
        cell1_1.setCellValue("거래명세서");
        cell1_1.setCellStyle(titleStyle(workbook));

        Cell cell1_6 = row1.createCell(6);
        cell1_6.setCellValue(DateUtils.YearMonthToString(yearMonth));
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
            if (i == 2) {
                row4.getCell(i).setCellValue(name);
            }
        }

        // 공급자(달리셔스) 정보
        TransactionInfoDefault transactionInfoDefault = getTransactionInfoDefault();
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
    }

    private void createDailyFoodHeader(Workbook workbook, Sheet sheet) {
        // 거래 내역 헤더
        Row row12 = sheet.createRow(12);
        String[] headers = {"일자", "메뉴명", "금액", "수량", "금액(vat별도)"};
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

    private Integer createHeaderTitle(Workbook workbook, Sheet sheet, Integer rowNum, String title) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(1);
        cell.setCellValue(title);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 1, 7));
        cell.setCellStyle(titleHeader(workbook));

        return ++rowNum;
    }

    private Integer createHeaderContext(Workbook workbook, Sheet sheet, Integer rowNum, String title) {
        Integer currantRow = createHeaderTitle(workbook, sheet, rowNum, title);

        // 거래 내역 헤더
        Row row = sheet.createRow(currantRow);
        String[] headers = {"일자", "항목", "금액", "수량", "일수", "금액(vat별도)"};
        for (int i = 1; i <= headers.length; i++) {
            Cell cell = null;
            if (i == 6) {
                cell = row.createCell(i);
                cell.setCellValue(headers[i - 1]);
                cell.setCellStyle(dataHeader(workbook));
                sheet.addMergedRegion(new CellRangeAddress(currantRow, currantRow, 6, 7));
                continue;
            }

            cell = row.createCell(i);
            cell.setCellValue(headers[i - 1]);

            cell.setCellStyle(dataHeader(workbook));
        }
        return ++currantRow;
    }

    private static void writePaycheckCategory(Workbook workBook, Row row, PaycheckCategory paycheckCategory) {
        CellStyle dataCellStyle = center(workBook);
        CellStyle priceCellStyle = priceStyle(workBook);

        Cell cell2 = row.createCell(2);
        cell2.setCellValue(paycheckCategory.getPaycheckCategoryItem().getPaycheckCategoryItem());
        cell2.setCellStyle(dataCellStyle);

        Cell cell3 = row.createCell(3);
        Integer priceValue = (paycheckCategory.getPrice() != null) ? paycheckCategory.getPrice().intValue() : null;
        if (priceValue != null) {
            cell3.setCellValue(priceValue);
        } else {
            cell3.setBlank();
        }
        cell3.setCellStyle(dataCellStyle);

        Cell cell4 = row.createCell(4);
        cell4.setCellValue(paycheckCategory.getCount());
        cell4.setCellStyle(dataCellStyle);

        //TODO: 수정 필요
        Cell cell5 = row.createCell(5);
        cell5.setCellValue(0);
        cell5.setCellStyle(dataCellStyle);

        Cell cell6 = row.createCell(6);
        cell6.setCellValue(paycheckCategory.getTotalPrice().intValue());
        cell6.setCellStyle(priceCellStyle);
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

    private static void createDailyFoodAddHeader(Workbook workBook, Sheet sheet, Row row) {
        String[] headers = {"이슈날짜", "내용", "금액"};
        for (int i = 1; i <= headers.length; i++) {
            Cell cell = null;
            if (i == 1) {
                cell = row.createCell(1);
                cell.setCellValue(headers[0]);
            }
            if (i == 2) {
                cell = row.createCell(2);
                cell.setCellValue(headers[1]);
                sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, 6));
            }
            if (i == 3) {
                cell = row.createCell(7);
                cell.setCellValue(headers[2]);
            }
            cell.setCellStyle(dataHeader(workBook));
        }
    }

    private static void writeDailyFoodAdd(Workbook workBook, Sheet sheet, Integer rowNum, PaycheckAdd paycheckAdd) {
        Row row = sheet.createRow(rowNum);
        CellStyle dataCellStyle = center(workBook);
        CellStyle priceCellStyle = priceStyle(workBook);

        Cell cell1 = row.createCell(1);
        cell1.setCellValue(DateUtils.format(paycheckAdd.getIssueDate()));
        cell1.setCellStyle(dataCellStyle);

        Cell cell2 = row.createCell(2);
        cell2.setCellValue(paycheckAdd.getMemo());
        cell2.setCellStyle(priceCellStyle);

        Cell cell3 = row.createCell(7);
        cell3.setCellValue(paycheckAdd.getPrice().intValue());
        cell3.setCellStyle(priceCellStyle);
    }

    private static Integer writeMakersTotalRow(Sheet sheet, Integer startRow, MakersPaycheck makersPaycheck) {
        // 총액 셀 추가
        Row totalPriceRow = sheet.createRow(startRow);


        Cell cell1 = totalPriceRow.createCell(6);
        cell1.setCellValue("총액");
        cell1.setCellStyle(boldCenter(sheet.getWorkbook()));

        Cell cell1_2 = totalPriceRow.createCell(7);
        cell1_2.setCellValue(makersPaycheck.getFoodTotalPrice().intValue());
        cell1_2.setCellStyle(boldPriceStyle(sheet.getWorkbook()));

        // 수수료 셀 추가
        Row chargeRow = sheet.createRow(startRow + 1);
        Cell cell2 = chargeRow.createCell(6);
        cell2.setCellValue("배송 수수료");
        cell2.setCellStyle(boldCenter(sheet.getWorkbook()));

        Cell cell2_2 = chargeRow.createCell(7);
        cell2_2.setCellValue(makersPaycheck.getCommissionPrice().intValue());
        cell2_2.setCellStyle(boldPriceStyle(sheet.getWorkbook()));

        // 결제 금액 셀 추가
        Row payPriceRow = sheet.createRow(startRow + 2);
        Cell cell3 = payPriceRow.createCell(6);
        cell3.setCellValue("Total");
        cell3.setCellStyle(boldCenter(sheet.getWorkbook()));

        // TODO: 수정필요
        Cell cell3_2 = payPriceRow.createCell(7);
        cell3_2.setCellValue(makersPaycheck.getTotalPrice().intValue());
        cell3_2.setCellStyle(priceStyle(sheet.getWorkbook()));

        return startRow + 3;
    }

    private static Integer writeCorporationTotalRow(Sheet sheet, Integer startRow, CorporationPaycheck corporationPaycheck) {
        // 총액 셀 추가
        Row row = sheet.createRow(startRow);

        Integer prepaidTotalPrice = corporationPaycheck.getExpectedPaycheck() == null ? 0 : corporationPaycheck.getExpectedTotalPrice().intValue();
        Integer totalPrice = corporationPaycheck.getTotalPrice().intValue();
        Integer paycheckAddPrice = corporationPaycheck.getPaycheckAdds() == null ? 0 : corporationPaycheck.getPaycheckAddsTotalPrice().intValue();

        if (prepaidTotalPrice != 0) {
            Cell cell = row.createCell(5);
            cell.setCellValue("선금 총액");

            Cell cell2 = row.createCell(6);
            cell2.setCellValue(prepaidTotalPrice);
            cell2.setCellStyle(priceStyle(sheet.getWorkbook()));
            sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 6, 7));

            row = sheet.createRow(++startRow);
        }

        Cell cell = row.createCell(5);
        cell.setCellValue("실비 총액");

        Cell cell2 = row.createCell(6);
        cell2.setCellValue(totalPrice + paycheckAddPrice);
        cell2.setCellStyle(priceStyle(sheet.getWorkbook()));
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 6, 7));

        Row row1 = sheet.createRow(++startRow);
        Cell cell3 = row1.createCell(5);
        cell3.setCellValue("총액 (VAT포함)");
        cell3.setCellStyle(bold(sheet.getWorkbook()));

        Cell cell4 = row1.createCell(6);
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 6, 7));

        // Create cell style
        CellStyle cellStyle = boldPriceStyle(sheet.getWorkbook());
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setTopBorderColor(IndexedColors.GREY_40_PERCENT.getIndex());

        // Apply cell style to cells 6 and 7 in the merged region
        for (int i = 6; i <= 7; i++) {
            Cell mergedCell = row1.createCell(i);
            mergedCell.setCellStyle(cellStyle);
        }

        cell4.setCellValue(1.1 * (totalPrice + paycheckAddPrice - prepaidTotalPrice));


        return ++startRow;
    }

    private Integer createCategoryTotalPrice(Sheet sheet, Integer rowNumber, BigDecimal totalPrice) {
        Row row = sheet.createRow(rowNumber);
        CellStyle borderStyle = sheet.getWorkbook().createCellStyle();
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setTopBorderColor(IndexedColors.GREY_40_PERCENT.getIndex());

        for (int i = 1; i <= 7; i++) {
            Cell cell = row.createCell(i);
            if (i == 6 || i == 7) {
                CellStyle cellStyle = boldPriceStyle(sheet.getWorkbook());
                cellStyle.setBorderTop(BorderStyle.THIN);
                cellStyle.setTopBorderColor(IndexedColors.GREY_40_PERCENT.getIndex());
                cell.setCellStyle(cellStyle);
            }
            else {
                row.getCell(i).setCellStyle(borderStyle);
            }
        }
        sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 6, 7));
        Cell cell = row.getCell(6);
        cell.setCellValue(totalPrice.intValue());
        return ++rowNumber;
    }

    private Integer createCategoryTotalPrice_2(Sheet sheet, Integer rowNumber, BigDecimal totalPrice) {
        Row row = sheet.createRow(rowNumber);
        CellStyle borderStyle = sheet.getWorkbook().createCellStyle();
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setTopBorderColor(IndexedColors.GREY_40_PERCENT.getIndex());

        for (int i = 1; i <= 7; i++) {
            Cell cell = row.createCell(i);
            if (i == 7) {
                CellStyle cellStyle = boldPriceStyle(sheet.getWorkbook());
                cellStyle.setBorderTop(BorderStyle.THIN);
                cellStyle.setTopBorderColor(IndexedColors.GREY_40_PERCENT.getIndex());
                cell.setCellStyle(cellStyle);
            }
            else {
                row.getCell(i).setCellStyle(borderStyle);
            }
        }
        Cell cell = row.getCell(7);
        cell.setCellValue(totalPrice.intValue());
        return ++rowNumber;
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

    private static CellStyle bold(Workbook workbook) {
        CellStyle bold = workbook.createCellStyle();
        bold.setVerticalAlignment(VerticalAlignment.CENTER);

        bold.setBorderTop(BorderStyle.THIN);

        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        bold.setFont(boldFont);
        return bold;
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

    private static CellStyle titleHeader(Workbook workbook) {
        XSSFCellStyle titleHeader = (XSSFCellStyle) workbook.createCellStyle();
        byte[] customColor = new byte[]{(byte) 123, (byte) 123, (byte) 123};
        XSSFColor xssfColor = new XSSFColor(customColor, new DefaultIndexedColorMap());
        titleHeader.setFillForegroundColor(xssfColor);
        titleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldFont.setColor(IndexedColors.WHITE.getIndex());
        titleHeader.setFont(boldFont);

        return titleHeader;
    }


    private byte[] getStamp() {
        return imageService.downloadImageFromS3("util/stamp.png");
    }

    private byte[] getLogo() {
        return imageService.downloadImageFromS3("util/logo.png");
    }

    public TransactionInfoDefault getTransactionInfoDefault() {
        return TransactionInfoDefault.builder()
                .businessNumber("376-87-00441")
                .address1("서울특별시 강남구 테헤란로 51길 21")
                .address2("3층(역삼동, 상경빌딩)")
                .corporationName("달리셔스 주식회사")
                .representative("이강용")
                .business("서비스 외")
                .phone("02-897-2123")
                .faxNumber("02-2179-9614")
                .businessForm("응용소프트웨어 개발 및 공급업 외")
                .build();
    }
}