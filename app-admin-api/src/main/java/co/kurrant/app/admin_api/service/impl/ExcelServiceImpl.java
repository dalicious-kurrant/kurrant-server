package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.food.dto.FoodListDto;
import co.dalicious.domain.food.dto.PresetScheduleDto;
import co.kurrant.app.admin_api.service.ExcelService;
import co.kurrant.app.admin_api.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

    private final ScheduleService scheduleService;

    @Override
    public List<FoodListDto> allFoodExcel(MultipartFile file) throws IOException {
        List<FoodListDto> dataList = new ArrayList<>();

        InputStream is = file.getInputStream();
        Tika tika = new Tika();
        String mimeType = tika.detect(is);
        if (isAllowedMIMEType(mimeType)) {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());

            Sheet worksheet = workbook.getSheetAt(0);

            for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) { // 1번째 행부터 끝까지
                Row row = worksheet.getRow(i);

                FoodListDto data = new FoodListDto();
                data.setFoodId(BigInteger.valueOf((long) row.getCell(0).getNumericCellValue()));
                data.setMakersName(String.valueOf(row.getCell(1).getStringCellValue()));
                data.setFoodName(String.valueOf(row.getCell(2).getStringCellValue()));
                data.setFoodStatus(String.valueOf(row.getCell(3).getStringCellValue()));
                data.setDefaultPrice(BigDecimal.valueOf(row.getCell(4).getNumericCellValue()));
                data.setMakersDiscount((int) row.getCell(5).getNumericCellValue());
                data.setEventDiscount((int) row.getCell(6).getNumericCellValue());
                data.setResultPrice(BigDecimal.valueOf(row.getCell(7).getNumericCellValue()));
                data.setDescription(String.valueOf(row.getCell(8).getStringCellValue()));

                //food tag
                String foodTagStr = String.valueOf(row.getCell(9).getStringCellValue());
                List<String> foodTagList = new ArrayList<>(List.of(foodTagStr.split(",")));
                data.setFoodTags(foodTagList);

                dataList.add(data);
            }
        }
        return dataList;
    }

    @Override
    public List<FoodListDto> makersFoodExcel(MultipartFile file) throws IOException {
        List<FoodListDto> dataList = new ArrayList<>();

        InputStream is = file.getInputStream();
        Tika tika = new Tika();
        String mimeType = tika.detect(is);
        if (isAllowedMIMEType(mimeType)) {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());

            Sheet worksheet = workbook.getSheetAt(0);

            for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) { // 1번째 행부터 끝까지
                Row row = worksheet.getRow(i);

                FoodListDto data = new FoodListDto();
                data.setFoodId(BigInteger.valueOf((long) row.getCell(0).getNumericCellValue()));
                data.setFoodImage(String.valueOf(row.getCell(1).getStringCellValue()));
                data.setFoodName(String.valueOf(row.getCell(2).getStringCellValue()));
                data.setFoodStatus(String.valueOf(row.getCell(3).getStringCellValue()));
                data.setDefaultPrice(BigDecimal.valueOf(row.getCell(4).getNumericCellValue()));
                data.setMakersDiscount((int) row.getCell(5).getNumericCellValue());
                data.setEventDiscount((int) row.getCell(6).getNumericCellValue());
                data.setResultPrice(BigDecimal.valueOf(row.getCell(7).getNumericCellValue()));
                data.setDescription(String.valueOf(row.getCell(8).getStringCellValue()));

                //food tag
                String foodTagStr = String.valueOf(row.getCell(9).getStringCellValue());
                List<String> foodTagList = new ArrayList<>(List.of(foodTagStr.split(",")));
                data.setFoodTags(foodTagList);

                dataList.add(data);
            }
        }
        return dataList;
    }

    @Override
    public void createMakersScheduleByExcel(MultipartFile file) throws IOException {
        List<PresetScheduleDto> dataList = new ArrayList<>();

        InputStream is = file.getInputStream();
        Tika tika = new Tika();
        String mimeType = tika.detect(is);
        if (isAllowedMIMEType(mimeType)) {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());

            Sheet worksheet = workbook.getSheetAt(0);

            for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) { // 1번째 행부터 끝까지
                Row row = worksheet.getRow(i);

                PresetScheduleDto data = new PresetScheduleDto();
                data.setServiceDate(String.valueOf(row.getCell(0).getStringCellValue()));
                data.setDiningType(String.valueOf(row.getCell(1).getStringCellValue()));
                data.setMakersName(String.valueOf(row.getCell(2).getStringCellValue()));
                data.setClientName(String.valueOf(row.getCell(3).getStringCellValue()));

                dataList.add(data);
            }
        }
        scheduleService.makePresetSchedules(dataList);
    }

    private boolean isAllowedMIMEType(String mimeType) {
        return mimeType.equals("application/x-tika-ooxml");
    }

}
