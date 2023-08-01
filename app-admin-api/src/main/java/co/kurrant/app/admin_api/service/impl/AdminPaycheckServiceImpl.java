package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.repository.CorporationRepository;
import co.dalicious.domain.file.dto.ImageResponseDto;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.file.service.ImageService;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.MakersRepository;
import co.dalicious.domain.order.dto.OrderCount;
import co.dalicious.domain.order.entity.DailyFoodSupportPrice;
import co.dalicious.domain.order.entity.MembershipSupportPrice;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.QOrderItemDailyFood;
import co.dalicious.domain.order.repository.QDailyFoodSupportPriceRepository;
import co.dalicious.domain.order.repository.QMembershipSupportPriceRepository;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.paycheck.dto.ExcelPdfDto;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.dto.TransactionInfoDefault;
import co.dalicious.domain.paycheck.entity.*;
import co.dalicious.domain.paycheck.entity.enums.PaycheckStatus;
import co.dalicious.domain.paycheck.mapper.CorporationPaycheckMapper;
import co.dalicious.domain.paycheck.mapper.MakersPaycheckMapper;
import co.dalicious.domain.paycheck.repository.*;
import co.dalicious.domain.paycheck.service.ExcelService;
import co.dalicious.domain.paycheck.service.PaycheckService;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.StringUtils;
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
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminPaycheckServiceImpl implements AdminPaycheckService {
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
    private final QMakersPaycheckRepository qMakersPaycheckRepository;
    private final SparkPlusLogRepository sparkPlusLogRepository;
    private final ExcelService excelService;
    private final QDailyFoodSupportPriceRepository qDailyFoodSupportPriceRepository;
    private final QMembershipSupportPriceRepository qMembershipSupportPriceRepository;
    private final QCorporationPaycheckRepository qCorporationPaycheckRepository;
    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    private final ExpectedPaycheckRepository expectedPaycheckRepository;
    private final QExpectedPaycheckRepository qExpectedPaycheckRepository;

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
        if (makersXlsx != null && !makersXlsx.isEmpty()) {
            ImageResponseDto excelFileDto = imageService.upload(makersXlsx, dirName);
            excelFile = new Image(excelFileDto);
        }

        Image pdfFile = null;
        if (makersXlsx != null && !makersPdf.isEmpty()) {
            ImageResponseDto pdfFileDto = imageService.upload(makersPdf, dirName);
            pdfFile = new Image(pdfFileDto);
        }

        MakersPaycheck makersPaycheck = makersPaycheckMapper.toEntity(paycheckDto, makers, excelFile, pdfFile);

        makersPaycheckRepository.save(makersPaycheck);
    }

    @Override
    @Transactional
    public PaycheckDto.MakersResponse getMakersPaychecks(Map<String, Object> parameters) {
        String startYearMonth = !parameters.containsKey("startYearMonth") || parameters.get("startYearMonth") == null ? null : String.valueOf(parameters.get("startYearMonth"));
        String endYearMonth = !parameters.containsKey("endYearMonth") || parameters.get("endYearMonth") == null ? null : String.valueOf(parameters.get("endYearMonth"));
        List<BigInteger> makersIds = !parameters.containsKey("makersIds") || parameters.get("makersIds").equals("") ? null : StringUtils.parseBigIntegerList((String) parameters.get("makersIds"));
        Integer status = !parameters.containsKey("status") || parameters.get("status") == null ? null : Integer.parseInt(String.valueOf(parameters.get("status")));
        Boolean hasRequest = !parameters.containsKey("hasRequest") || parameters.get("hasRequest") == null ? null : Boolean.valueOf(String.valueOf(parameters.get("hasRequest")));

        YearMonth start = startYearMonth == null ? null : YearMonth.parse(startYearMonth.substring(0, 4) + "-" + startYearMonth.substring(4));
        YearMonth end = endYearMonth == null ? null : YearMonth.parse(endYearMonth.substring(0, 4) + "-" + endYearMonth.substring(4));

        List<MakersPaycheck> makersPaychecks = qMakersPaycheckRepository.getMakersPaychecksByFilter(start, end, makersIds, PaycheckStatus.ofCode(status), hasRequest);
        return makersPaycheckMapper.toMakersResponse(makersPaychecks);
    }

    @Override
    @Transactional
    public PaycheckDto.MakersDetail getMakersPaycheckDetail(BigInteger makersPaycheckId) {
        MakersPaycheck makersPaycheck = makersPaycheckRepository.findById(makersPaycheckId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        TransactionInfoDefault transactionInfoDefault = paycheckService.getTransactionInfoDefault();
        transactionInfoDefault.updateYearMonth(DateUtils.YearMonthToString(makersPaycheck.getYearMonth()));
        return makersPaycheckMapper.toDetailDto(makersPaycheck, transactionInfoDefault);
    }

//    @Override
//    @Transactional
//    public void updateMakersPaycheck(MultipartFile makersXlsx, MultipartFile makersPdf, PaycheckDto.MakersResponse paycheckDto) throws IOException {
//        MakersPaycheck makersPaycheck = makersPaycheckRepository.findById(paycheckDto.getId())
//                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
//
//        // 요청 Body에 파일이 존재하지 않는다면 삭제
//        if (paycheckDto.getExcelFile() == null && makersPaycheck.getExcelFile() != null) {
//            imageService.delete(getImagePrefix(makersPaycheck.getExcelFile()));
//            makersPaycheck.updateExcelFile(null);
//        }
//        if (paycheckDto.getPdfFile() == null && makersPaycheck.getPdfFile() != null) {
//            imageService.delete(getImagePrefix(makersPaycheck.getPdfFile()));
//            makersPaycheck.updatePdfFile(null);
//        }
//
//        // 업로드 파일 저장
//        if (makersXlsx != null) {
//            String dirName = "paycheck/makers/" + paycheckDto.getId().toString() + "/" + paycheckDto.getYear().toString() + paycheckDto.getMonth().toString();
//            ImageResponseDto excelFileDto = imageService.upload(makersXlsx, dirName);
//            Image excelFile = new Image(excelFileDto);
//            makersPaycheck.updateExcelFile(excelFile);
//        }
//        if (makersPdf != null) {
//            String dirName = "paycheck/makers/" + paycheckDto.getId().toString() + "/" + paycheckDto.getYear().toString() + paycheckDto.getMonth().toString();
//            ImageResponseDto pdfFileDto = imageService.upload(makersPdf, dirName);
//            Image pdfFile = new Image(pdfFileDto);
//            makersPaycheck.updatePdfFile(pdfFile);
//        }
//
//        makersPaycheck.updateMakersPaycheck(YearMonth.of(paycheckDto.getYear(), paycheckDto.getMonth()), PaycheckStatus.ofString(paycheckDto.getPaycheckStatus()));
//    }

    @Override
    public void deleteMakersPaycheck(PaycheckDto.Request request) {
        YearMonth yearMonth = DateUtils.stringToYearMonth(request.getDate());
        List<MakersPaycheck> makersPaychecks = qMakersPaycheckRepository.getMakersPaychecksByFilter(request.getId(), yearMonth);
        for (MakersPaycheck makersPaycheck : makersPaychecks) {
            imageService.delete(getImagePrefix(makersPaycheck.getExcelFile()));
            imageService.delete(getImagePrefix(makersPaycheck.getPdfFile()));
        }
        makersPaycheckRepository.deleteAll(makersPaychecks);
    }

    @Override
    public void deleteMakersPaycheckById(BigInteger makersPaycheckId) {
        MakersPaycheck makersPaycheck = makersPaycheckRepository.findById(makersPaycheckId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        imageService.delete(getImagePrefix(makersPaycheck.getExcelFile()));
        imageService.delete(getImagePrefix(makersPaycheck.getPdfFile()));
        makersPaycheckRepository.delete(makersPaycheck);
    }
    //메이커스 정산 삭제

    @Override
    @Transactional
    public void postMakersPaycheckAdd(BigInteger makersPaycheckId, List<PaycheckDto.PaycheckAddDto> paycheckAddDtos) {
        MakersPaycheck makersPaycheck = makersPaycheckRepository.findById(makersPaycheckId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        List<PaycheckAdd> paycheckAdds = makersPaycheckMapper.toPaycheckAdds(paycheckAddDtos);
        makersPaycheck = makersPaycheck.updatePaycheckAdds(paycheckAdds);

        // 요청 Body에 파일이 존재하지 않는다면 삭제
        if (makersPaycheck.getExcelFile() != null) {
            imageService.delete(getImagePrefix(makersPaycheck.getExcelFile()));
            imageService.delete(getImagePrefix(makersPaycheck.getPdfFile()));
        }
        // 수정된 정산 S3에 업로드 후 Entity 엑셀 파일 경로 저장
        ExcelPdfDto excelPdfDto = excelService.createMakersPaycheckExcel(makersPaycheck);
        Image excel = new Image(excelPdfDto.getExcelDto());
        Image pdf = new Image(excelPdfDto.getPdfDto());
        makersPaycheck.updateExcelFile(excel);
        makersPaycheck.updatePdfFile(pdf);
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
    public void postMakersMemo(BigInteger paycheckId, PaycheckDto.MemoDto memoDto) {
        MakersPaycheck makersPaycheck = makersPaycheckRepository.findById(paycheckId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        String writer = "커런트";
        PaycheckMemo paycheckMemo = new PaycheckMemo(writer, memoDto.getMemo());
        makersPaycheck.updateMemo(paycheckMemo);
    }

    @Override
    @Transactional
    public void postCorporationPaycheckExcel(PaycheckDto.Request request) {
        YearMonth yearMonth = DateUtils.stringToYearMonth(request.getDate());
        // 기존에 존재하던 정산 삭제
        List<CorporationPaycheck> corporationPaychecks = qCorporationPaycheckRepository.getCorporationPaychecksByFilter(request.getId(), yearMonth);
        List<ExpectedPaycheck> expectedPaychecks = qExpectedPaycheckRepository.findAllByCorporationPaychecks(corporationPaychecks);
        for (CorporationPaycheck corporationPaycheck : corporationPaychecks) {
            imageService.delete(getImagePrefix(corporationPaycheck.getExcelFile()));
            imageService.delete(getImagePrefix(corporationPaycheck.getPdfFile()));
        }
        expectedPaycheckRepository.deleteAll(expectedPaychecks);
        corporationPaycheckRepository.deleteAll(corporationPaychecks);

        List<DailyFoodSupportPrice> dailyFoodSupportPrices = qDailyFoodSupportPriceRepository.findAllByGroupsAndPeriod(request.getId(), yearMonth);
        List<MembershipSupportPrice> membershipSupportPrices = qMembershipSupportPriceRepository.findAllByGroupIdsAndPeriod(request.getId(), yearMonth);

        MultiValueMap<Group, DailyFoodSupportPrice> dailyFoodSupportPriceMap = new LinkedMultiValueMap<>();
        MultiValueMap<Group, MembershipSupportPrice> membershipSupportPriceMap = new LinkedMultiValueMap<>();

        Set<Group> groups = new HashSet<>();
        Set<Group> garbageUseGroups = new HashSet<>();

        for (DailyFoodSupportPrice dailyFoodSupportPrice : dailyFoodSupportPrices) {
            dailyFoodSupportPriceMap.add(dailyFoodSupportPrice.getGroup(), dailyFoodSupportPrice);
            groups.add(dailyFoodSupportPrice.getGroup());
        }
        for (Group group : groups) {
            if (((Corporation) Hibernate.unproxy(group)).getIsGarbage()) {
                garbageUseGroups.add(group);
            }
        }
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findAllOrderItemDailyFoodCount(groups, yearMonth);
        List<OrderCount> orderCounts = OrderUtil.getTotalOrderCount(orderItemDailyFoods);

        for (MembershipSupportPrice membershipSupportPrice : membershipSupportPrices) {
            membershipSupportPriceMap.add(membershipSupportPrice.getGroup(), membershipSupportPrice);
        }

        for (Group group : groups) {
            OrderCount orderCount = orderCounts.stream()
                    .filter(v -> v.getGroup().equals(group))
                    .findAny()
                    .orElse(null);
            CorporationPaycheck corporationPaycheck = paycheckService.generateCorporationPaycheck((Corporation) Hibernate.unproxy(group), dailyFoodSupportPriceMap.get(group), membershipSupportPriceMap.get(group), orderCount, yearMonth);
            ExcelPdfDto excelPdfDto = excelService.createCorporationPaycheckExcel(corporationPaycheck, corporationPaycheckMapper.toCorporationOrder((Corporation) Hibernate.unproxy(group), dailyFoodSupportPriceMap.get(group)));
            Image excel = new Image(excelPdfDto.getExcelDto());
            Image pdf = new Image(excelPdfDto.getPdfDto());
            corporationPaycheck.updateExcelFile(excel);
            corporationPaycheck.updatePdfFile(pdf);
        }
    }

    @Override
    @Transactional
    public void postOneCorporationPaycheckExcel(BigInteger corporationId, String yearMonthStr) {
        Corporation corporation = corporationRepository.findById(corporationId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        YearMonth yearMonth = DateUtils.stringToYearMonth(yearMonthStr);
        List<DailyFoodSupportPrice> dailyFoodSupportPrices = qDailyFoodSupportPriceRepository.findAllByGroupAndPeriod(corporation, yearMonth.atDay(1), yearMonth.atEndOfMonth());
        List<MembershipSupportPrice> membershipSupportPrices = qMembershipSupportPriceRepository.findAllByGroupAndPeriod(corporation, yearMonth);

        /*
        CorporationPaycheck corporationPaycheck = paycheckService.generateCorporationPaycheck(corporation, dailyFoodSupportPrices, membershipSupportPrices, yearMonth);
        if(corporationPaycheck != null) {
            ExcelPdfDto excelPdfDto = excelService.createCorporationPaycheckExcel(corporationPaycheck, corporationPaycheckMapper.toCorporationOrder(corporation, dailyFoodSupportPrices));
            Image excel = new Image(excelPdfDto.getExcelDto());
            Image pdf = new Image(excelPdfDto.getPdfDto());
            corporationPaycheck.updateExcelFile(excel);
            corporationPaycheck.updatePdfFile(pdf);
        }
        */
    }

    @Override
    @Transactional
    public PaycheckDto.CorporationMain getCorporationPaychecks(Map<String, Object> parameters) {
        String startYearMonth = !parameters.containsKey("startYearMonth") || parameters.get("startYearMonth") == null ? null : String.valueOf(parameters.get("startYearMonth"));
        String endYearMonth = !parameters.containsKey("endYearMonth") || parameters.get("endYearMonth") == null ? null : String.valueOf(parameters.get("endYearMonth"));
        List<BigInteger> corporationIds = !parameters.containsKey("corporationIds") || parameters.get("corporationIds").equals("") ? null : StringUtils.parseBigIntegerList((String) parameters.get("corporationIds"));
        Integer status = !parameters.containsKey("status") || parameters.get("status") == null ? null : Integer.parseInt(String.valueOf(parameters.get("status")));
        Boolean hasRequest = !parameters.containsKey("hasRequest") || parameters.get("hasRequest") == null ? null : Boolean.valueOf(String.valueOf(parameters.get("hasRequest")));

        YearMonth start = startYearMonth == null ? null : YearMonth.parse(startYearMonth.substring(0, 4) + "-" + startYearMonth.substring(4));
        YearMonth end = endYearMonth == null ? null : YearMonth.parse(endYearMonth.substring(0, 4) + "-" + endYearMonth.substring(4));


        List<CorporationPaycheck> corporationPaychecks = qCorporationPaycheckRepository.getCorporationPaychecksByFilter(start, end, corporationIds, PaycheckStatus.ofCode(status), hasRequest);
        return corporationPaycheckMapper.toListDto(corporationPaychecks);
    }

    @Override
    @Transactional
    public PaycheckDto.CorporationOrder getCorporationOrderHistory(BigInteger corporationPaycheckId) {
        CorporationPaycheck corporationPaycheck = corporationPaycheckRepository.findById(corporationPaycheckId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        Corporation corporation = corporationPaycheck.getCorporation();
        YearMonth yearMonth = corporationPaycheck.getYearMonth();
        List<DailyFoodSupportPrice> dailyFoodSupportPrices = qDailyFoodSupportPriceRepository.findAllByGroupAndPeriod(corporation, yearMonth.atDay(1), yearMonth.atEndOfMonth());
        return corporationPaycheckMapper.toCorporationOrder(corporation, dailyFoodSupportPrices);
    }

    @Override
    @Transactional
    public PaycheckDto.Invoice getCorporationInvoice(BigInteger corporationPaycheckId) {
        CorporationPaycheck corporationPaycheck = corporationPaycheckRepository.findById(corporationPaycheckId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        PaycheckDto.Invoice invoice = corporationPaycheckMapper.toInvoice(corporationPaycheck, 0);
        TransactionInfoDefault transactionInfoDefault = paycheckService.getTransactionInfoDefault();
        transactionInfoDefault.setYearMonth(DateUtils.YearMonthToString(corporationPaycheck.getYearMonth()));
        invoice.setTransactionInfoDefault(transactionInfoDefault);
        return invoice;
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
    public void deleteCorporationPaycheck(PaycheckDto.Request request) {
        YearMonth yearMonth = DateUtils.stringToYearMonth(request.getDate());
        List<CorporationPaycheck> corporationPaychecks = qCorporationPaycheckRepository.getCorporationPaychecksByFilter(request.getId(), yearMonth);
        List<ExpectedPaycheck> expectedPaychecks = qExpectedPaycheckRepository.findAllByCorporationPaychecks(corporationPaychecks);
        for (CorporationPaycheck corporationPaycheck : corporationPaychecks) {
            imageService.delete(getImagePrefix(corporationPaycheck.getExcelFile()));
            imageService.delete(getImagePrefix(corporationPaycheck.getPdfFile()));
        }
        expectedPaycheckRepository.deleteAll(expectedPaychecks);
        corporationPaycheckRepository.deleteAll(corporationPaychecks);
    }

    @Override
    public void deleteCorporationPaycheckById(BigInteger corporationPaycheckId) {
        CorporationPaycheck corporationPaycheck = corporationPaycheckRepository.findById(corporationPaycheckId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        ExpectedPaycheck expectedPaycheck = corporationPaycheck.getExpectedPaycheck();

        imageService.delete(getImagePrefix(corporationPaycheck.getExcelFile()));
        imageService.delete(getImagePrefix(corporationPaycheck.getPdfFile()));

        if (expectedPaycheck != null) expectedPaycheckRepository.delete(expectedPaycheck);
        corporationPaycheckRepository.delete(corporationPaycheck);
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
    public void postCorporationPaycheckAdd(BigInteger corporationPaycheckId, List<PaycheckDto.PaycheckAddDto> paycheckAddDtos) {
        CorporationPaycheck corporationPaycheck = corporationPaycheckRepository.findById(corporationPaycheckId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        List<PaycheckAdd> paycheckAdds = corporationPaycheckMapper.toMemoPaycheckAdds(paycheckAddDtos);
        Corporation corporation = corporationPaycheck.getCorporation();
        corporationPaycheck = corporationPaycheck.updatePaycheckAdds(paycheckAdds);

        YearMonth yearMonth = corporationPaycheck.getYearMonth();
        List<DailyFoodSupportPrice> dailyFoodSupportPrices = qDailyFoodSupportPriceRepository.findAllByGroupAndPeriod(corporation, yearMonth.atDay(1), yearMonth.atEndOfMonth());

        // 요청 Body에 파일이 존재하지 않는다면 삭제
        if (corporationPaycheck.getExcelFile() != null) {
            imageService.delete(getImagePrefix(corporationPaycheck.getExcelFile()));
            imageService.delete(getImagePrefix(corporationPaycheck.getPdfFile()));
        }
        // 수정된 정산 S3에 업로드 후 Entity 엑셀 파일 경로 저장
        ExcelPdfDto excelPdfDto = excelService.createCorporationPaycheckExcel(corporationPaycheck, corporationPaycheckMapper.toCorporationOrder(corporation, dailyFoodSupportPrices));
        Image excel = new Image(excelPdfDto.getExcelDto());
        Image pdf = new Image(excelPdfDto.getPdfDto());
        corporationPaycheck.updateExcelFile(excel);
        corporationPaycheck.updatePdfFile(pdf);
    }

    @Override
    @Transactional
    public void postCorporationMemo(BigInteger paycheckId, PaycheckDto.MemoDto memoDto) {
        CorporationPaycheck corporationPaycheck = corporationPaycheckRepository.findById(paycheckId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        String writer = "커런트";
        PaycheckMemo paycheckMemo = new PaycheckMemo(writer, memoDto.getMemo());
        corporationPaycheck.updateMemo(paycheckMemo);
    }

    @Override
    @Transactional
    public void postSparkplusLog(Integer log) {
        SparkPlusLogType sparkPlusLogType = SparkPlusLogType.ofCode(log);
        SparkPlusLog sparkPlusLog = sparkPlusLogRepository.findOneBySparkPlusLogType(sparkPlusLogType)
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));

        sparkPlusLog.addCount();
    }

    @Override
    public List<SparkPlusLog> getSpartplusLog() {
        return sparkPlusLogRepository.findAll();
    }


    @Override
    @Transactional
    public List<MakersPaycheck> postMakersPaycheckExcel(PaycheckDto.Request request) {
        YearMonth yearMonth = DateUtils.stringToYearMonth(request.getDate());
        List<MakersPaycheck> makersPaychecks = qMakersPaycheckRepository.getMakersPaychecksByFilter(request.getId(), yearMonth);
        for (MakersPaycheck makersPaycheck : makersPaychecks) {
            imageService.delete(getImagePrefix(makersPaycheck.getExcelFile()));
            imageService.delete(getImagePrefix(makersPaycheck.getPdfFile()));
        }
        makersPaycheckRepository.deleteAll(makersPaychecks);
        return paycheckService.generateAllMakersPaycheck(qMakersPaycheckRepository.getPaycheckDto(yearMonth, request.getId()), yearMonth);
    }

    @Override
    @Transactional
    public void postOneMakersPaycheckExcel(BigInteger makersId, String yearMonthStr) {
        Makers makers = makersRepository.findById(makersId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS));
        YearMonth yearMonth = DateUtils.stringToYearMonth(yearMonthStr);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        List<Integer> diningTypes = List.of(1, 2, 3);
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findAllByMakersFilter(start, end, makers, diningTypes);
        MakersPaycheck makersPaycheck = paycheckService.generateMakersPaycheck(makers, orderItemDailyFoods);
        // 정산 엑셀 생성
        ExcelPdfDto excelPdfDto = excelService.createMakersPaycheckExcel(makersPaycheck);
        Image excelFile = new Image(excelPdfDto.getExcelDto());
        Image pdfFile = new Image(excelPdfDto.getPdfDto());
        makersPaycheck.updateExcelFile(excelFile);
        makersPaycheck.updatePdfFile(pdfFile);
    }

    public String getImagePrefix(Image image) {
        StringBuilder preifx = new StringBuilder();
        String[] str = image.getLocation().split("/");
        for (int i = 3; i < str.length - 1; i++) {
            preifx.append(str[i]).append("/");
        }
        return preifx.toString();
    }
}
