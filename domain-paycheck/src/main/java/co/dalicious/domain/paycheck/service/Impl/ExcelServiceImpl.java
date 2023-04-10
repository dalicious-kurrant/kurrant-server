package co.dalicious.domain.paycheck.service.Impl;

import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import co.dalicious.domain.paycheck.entity.PaycheckDailyFood;
import co.dalicious.domain.paycheck.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {
    @Override
    public void createMakersPaycheckExcel(MakersPaycheck makersPaycheck) {
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("MakersPaycheck");

        String fileName = makersPaycheck.getYearMonth().getYear() +
                ((makersPaycheck.getYearMonth().getMonthValue() < 10) ? "0" + String.valueOf(makersPaycheck.getYearMonth().getMonthValue()) : String.valueOf(makersPaycheck.getYearMonth().getMonthValue()))  +
                makersPaycheck.getMakers().getName();

        // Create header rows
        createHeaderRows(sheet);

        // Write daily foods data
        int currentRow = 4;
        List<PaycheckDailyFood> dailyFoods = makersPaycheck.getPaycheckDailyFoods();
        for (PaycheckDailyFood dailyFood : dailyFoods) {
            Row row = sheet.createRow(currentRow);
            writeDailyFood(row, dailyFood);
            currentRow++;
        }

        // Write total row
        Row totalRow = sheet.createRow(currentRow);
        writeTotalRow(totalRow, dailyFoods);

        // Adjust column widths
        for (int i = 0; i < 7; i++) {
            sheet.autoSizeColumn(i);
        }

        // Merge cells
        mergeCells(sheet);

        // Save the file locally
        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createHeaderRows(Sheet sheet) {
        Row row1 = sheet.createRow(0);
        row1.createCell(0).setCellValue("거래명세서");

        Row row2 = sheet.createRow(1);
        row2.createCell(0).setCellValue("2023-03");

        Row row4 = sheet.createRow(3);
        String[] headers = {"일자", "메뉴명", "금액", "수량", "금액(vat포함)", "수수료율", "수수료"};
        for (int i = 0; i < headers.length; i++) {
            row4.createCell(i).setCellValue(headers[i]);
        }
    }

    private static void writeDailyFood(Row row, PaycheckDailyFood dailyFood) {
        row.createCell(0).setCellValue(dailyFood.getServiceDate().toString());
        row.createCell(1).setCellValue(dailyFood.getName());
        row.createCell(2).setCellValue(dailyFood.getSupplyPrice().toString());
        row.createCell(3).setCellValue(dailyFood.getCount());
        row.createCell(4).setCellValue(dailyFood.getTotalPrice().toString());
        row.createCell(5).setCellValue("0.0%");
        row.createCell(6).setCellValue("-");
    }

    private static void writeTotalRow(Row row, List<PaycheckDailyFood> dailyFoods) {
        BigDecimal total = dailyFoods.stream()
                .map(df -> df.getSupplyPrice().multiply(BigDecimal.valueOf(df.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        row.createCell(4).setCellValue("Total");
        row.createCell(5).setCellValue(total.toString());
    }

    private static void mergeCells(Sheet sheet) {
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));
    }
}
