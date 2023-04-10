package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.repository.CorporationRepository;
import co.dalicious.domain.file.dto.ImageResponseDto;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.file.service.ImageService;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.MakersRepository;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.entity.CorporationPaycheck;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import co.dalicious.domain.paycheck.entity.enums.PaycheckStatus;
import co.dalicious.domain.paycheck.mapper.CorporationPaycheckMapper;
import co.dalicious.domain.paycheck.mapper.MakersPaycheckMapper;
import co.dalicious.domain.paycheck.repository.CorporationPaycheckRepository;
import co.dalicious.domain.paycheck.repository.MakersPaycheckRepository;
import co.dalicious.domain.paycheck.service.ExcelService;
import co.dalicious.domain.paycheck.service.PaycheckService;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.MakersDto;
import co.dalicious.domain.client.entity.SparkPlusLog;
import co.dalicious.domain.client.entity.enums.SparkPlusLogType;
import co.kurrant.app.admin_api.mapper.GroupMapper;
import co.kurrant.app.admin_api.mapper.MakersMapper;
import co.dalicious.domain.client.repository.SparkPlusLogRepository;
import co.kurrant.app.admin_api.service.AdminPaycheckService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAdminPaycheckServiceImpl implements AdminPaycheckService {
    private final MakersRepository makersRepository;
    private final ImageService imageService;
    private final MakersPaycheckMapper makersPaycheckMapper;
    private final MakersPaycheckRepository makersPaycheckRepository;
    private final MakersMapper makersMapper;
    private final CorporationRepository corporationRepository;
    private final GroupMapper groupMapper;
    private final CorporationPaycheckMapper corporationPaycheckMapper;
    private final CorporationPaycheckRepository corporationPaycheckRepository;
    private final PaycheckService paycheckService;
    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    private final ExcelService excelService;
    private final SparkPlusLogRepository sparkPlusLogRepository;

    @Override
    @Transactional
    public List<MakersDto.Makers> getMakers() {
        List<Makers> makersList = makersRepository.findAll();
        return makersMapper.makersToDtos(makersList);
    }

    @Override
    @Transactional
    public List<GroupDto.Group> getCorporations() {
        List<Corporation> corporations = corporationRepository.findAll();
        return groupMapper.groupsToDtos(corporations);
    }

    @Override
    @Transactional
    public void postMakersPaycheck(MultipartFile makersXlsx, MultipartFile makersPdf, PaycheckDto.MakersRequest paycheckDto) throws IOException {
        Makers makers = makersRepository.findById(paycheckDto.getMakersId())
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS));

        String dirName = "paycheck/makers/" + paycheckDto.getMakersId().toString() + "/" + paycheckDto.getYear().toString() + paycheckDto.getMonth().toString();

        Image excelFile = null;
        if(makersXlsx != null && !makersXlsx.isEmpty()) {
            ImageResponseDto excelFileDto = imageService.upload(makersXlsx, dirName);
            excelFile = new Image(excelFileDto);
        }

        Image pdfFile = null;
        if(makersXlsx != null && !makersPdf.isEmpty()) {
            ImageResponseDto pdfFileDto = imageService.upload(makersPdf, dirName);
            pdfFile = new Image(pdfFileDto);
        }

        MakersPaycheck makersPaycheck = makersPaycheckMapper.toEntity(paycheckDto, makers, excelFile, pdfFile);

        makersPaycheckRepository.save(makersPaycheck);
    }

    @Override
    @Transactional
    public List<PaycheckDto.MakersResponse> getMakersPaychecks() {
        List<MakersPaycheck> makersPaychecks = makersPaycheckRepository.findAllByOrderByCreatedDateTimeDesc();
        return makersPaycheckMapper.toDtos(makersPaychecks);
    }

    @Override
    @Transactional
    public void updateMakersPaycheck(MultipartFile makersXlsx, MultipartFile makersPdf, PaycheckDto.MakersResponse paycheckDto) throws IOException {
        MakersPaycheck makersPaycheck = makersPaycheckRepository.findById(paycheckDto.getId())
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));

        // 요청 Body에 파일이 존재하지 않는다면 삭제
        if (paycheckDto.getExcelFile() == null && makersPaycheck.getExcelFile() != null) {
            imageService.delete(getImagePrefix(makersPaycheck.getExcelFile()));
            makersPaycheck.updateExcelFile(null);
        }
        if (paycheckDto.getPdfFile() == null && makersPaycheck.getPdfFile() != null) {
            imageService.delete(getImagePrefix(makersPaycheck.getPdfFile()));
            makersPaycheck.updatePdfFile(null);
        }

        // 업로드 파일 저장
        if (makersXlsx != null) {
            String dirName = "paycheck/makers/" + paycheckDto.getId().toString() + "/" + paycheckDto.getYear().toString() + paycheckDto.getMonth().toString();
            ImageResponseDto excelFileDto = imageService.upload(makersXlsx, dirName);
            Image excelFile = new Image(excelFileDto);
            makersPaycheck.updateExcelFile(excelFile);
        }
        if (makersPdf != null) {
            String dirName = "paycheck/makers/" + paycheckDto.getId().toString() + "/" + paycheckDto.getYear().toString() + paycheckDto.getMonth().toString();
            ImageResponseDto pdfFileDto = imageService.upload(makersPdf, dirName);
            Image pdfFile = new Image(pdfFileDto);
            makersPaycheck.updatePdfFile(pdfFile);
        }

        makersPaycheck.updateMakersPaycheck(YearMonth.of(paycheckDto.getYear(), paycheckDto.getMonth()), PaycheckStatus.ofString(paycheckDto.getPaycheckStatus()));
    }

    @Override
    public void deleteMakersPaycheck(List<BigInteger> ids) {
        List<MakersPaycheck> makersPaychecks = makersPaycheckRepository.findAllByIdIn(ids);
        makersPaycheckRepository.deleteAll(makersPaychecks);
    }

    @Override
    @Transactional
    public void updateMakersPaycheckStatus(Integer status, List<BigInteger> ids) {
        PaycheckStatus paycheckStatus = PaycheckStatus.ofCode(status);
        if (paycheckStatus == null) {
            throw new ApiException(ExceptionEnum.ENUM_NOT_FOUND);
        }

        List<MakersPaycheck> makersPaychecks = makersPaycheckRepository.findAllByIdIn(ids);

        for (MakersPaycheck makersPaycheck : makersPaychecks) {
            makersPaycheck.updatePaycheckStatus(paycheckStatus);
        }
    }

    @Override
    @Transactional
    public void postCorporationPaycheck(MultipartFile corporationXlsx, MultipartFile corporationPdf, PaycheckDto.CorporationRequest paycheckDto) throws IOException {
        Corporation corporation = corporationRepository.findById(paycheckDto.getCorporationId())
                .orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));

        String dirName = "paycheck/corporations/" + paycheckDto.getCorporationId().toString() + "/" + paycheckDto.getYear().toString() + paycheckDto.getMonth().toString();

        Image excelFile = null;
        if(corporationXlsx != null && !corporationXlsx.isEmpty()) {
            ImageResponseDto excelFileDto = imageService.upload(corporationXlsx, dirName);
            excelFile = new Image(excelFileDto);
        }

        Image pdfFile = null;
        if(corporationPdf != null && !corporationPdf.isEmpty()) {
            ImageResponseDto pdfFileDto = imageService.upload(corporationPdf, dirName);
            pdfFile = new Image(pdfFileDto);
        }

        CorporationPaycheck corporationPaycheck = corporationPaycheckMapper.toEntity(paycheckDto, corporation, excelFile, pdfFile);
        corporationPaycheckRepository.save(corporationPaycheck);
    }

    @Override
    @Transactional
    public List<PaycheckDto.CorporationResponse> getCorporationPaychecks() {
        List<CorporationPaycheck> corporationPaychecks = corporationPaycheckRepository.findAll();
        return corporationPaycheckMapper.toDtos(corporationPaychecks);
    }

    @Override
    @Transactional
    public void updateCorporationPaycheck(MultipartFile corporationXlsx, MultipartFile corporationPdf, PaycheckDto.CorporationResponse paycheckDto) throws IOException {
        CorporationPaycheck corporationPaycheck = corporationPaycheckRepository.findById(paycheckDto.getId())
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));

        // 요청 Body에 파일이 존재하지 않는다면 삭제
        if (paycheckDto.getExcelFile() == null && corporationPaycheck.getExcelFile() != null) {
            imageService.delete(getImagePrefix(corporationPaycheck.getExcelFile()));
            corporationPaycheck.updateExcelFile(null);

        }
        if (paycheckDto.getPdfFile() == null && corporationPaycheck.getPdfFile() != null) {
            imageService.delete(getImagePrefix(corporationPaycheck.getPdfFile()));
            corporationPaycheck.updatePdfFile(null);
        }

        // 업로드 파일 저장
        if (corporationXlsx != null) {
            String dirName = "paycheck/corporations/" + paycheckDto.getId().toString() + "/" + paycheckDto.getYear().toString() + paycheckDto.getMonth().toString();
            ImageResponseDto excelFileDto = imageService.upload(corporationXlsx, dirName);
            Image excelFile = new Image(excelFileDto);
            corporationPaycheck.updateExcelFile(excelFile);
        }
        if (corporationPdf != null) {
            String dirName = "paycheck/corporations/" + paycheckDto.getId().toString() + "/" + paycheckDto.getYear().toString() + paycheckDto.getMonth().toString();
            ImageResponseDto pdfFileDto = imageService.upload(corporationPdf, dirName);
            Image pdfFile = new Image(pdfFileDto);
            corporationPaycheck.updatePdfFile(pdfFile);
        }

        corporationPaycheck.updateCorporationPaycheck(
                YearMonth.of(paycheckDto.getYear(), paycheckDto.getMonth()),
                PaycheckStatus.ofString(paycheckDto.getPaycheckStatus()),
                paycheckDto.getManagerName(),
                paycheckDto.getPhone());
    }

    @Override
    public void deleteCorporationPaycheck(List<BigInteger> ids) {
        List<CorporationPaycheck> corporationPaychecks = corporationPaycheckRepository.findAllByIdIn(ids);
        corporationPaycheckRepository.deleteAll(corporationPaychecks);
    }

    @Override
    @Transactional
    public void updateCorporationPaycheckStatus(Integer status, List<BigInteger> ids) {
        PaycheckStatus paycheckStatus = PaycheckStatus.ofCode(status);
        if (paycheckStatus == null) {
            throw new ApiException(ExceptionEnum.ENUM_NOT_FOUND);
        }

        List<CorporationPaycheck> corporationPaychecks = corporationPaycheckRepository.findAllByIdIn(ids);

        for (CorporationPaycheck corporationPaycheck : corporationPaychecks) {
            corporationPaycheck.updatePaycheckStatus(paycheckStatus);
        }
    }

    @Override
    @Transactional
    public void postSparkplusLog(Integer log) {
        SparkPlusLogType sparkPlusLogType = SparkPlusLogType.ofCode(log);
        SparkPlusLog sparkPlusLog =  sparkPlusLogRepository.findOneBySparkPlusLogType(sparkPlusLogType)
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));

        sparkPlusLog.addCount();
    }

    @Override
    public List<SparkPlusLog> getSpartplusLog() {
        return sparkPlusLogRepository.findAll();
    }


    @Override
    public void postMakersPaycheckExcel() {
        Makers makers = makersRepository.findById(BigInteger.valueOf(1))
                        .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS));
        LocalDate startDate = LocalDate.of(2023, 4, 1);
        LocalDate endDate = LocalDate.of(2023, 4, 30);
        List<Integer> diningTypes = Arrays.asList(1, 2, 3);
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findAllByMakersFilter(startDate, endDate, makers, diningTypes);
        MakersPaycheck makersPaycheck = paycheckService.generateMakersPaycheck(makers, orderItemDailyFoods);
        makersPaycheck = makersPaycheckRepository.save(makersPaycheck);

        excelService.createMakersPaycheckExcel(makersPaycheck);
    }

    public String getImagePrefix(Image image) {
        StringBuilder preifx = new StringBuilder();
        String[] str = image.getLocation().split("/");
        for(int i = 3; i < str.length - 1; i ++) {
            preifx.append(str[i]).append("/");
        }
        return preifx.toString();
    }
}
