package co.kurrant.app.admin_api.service;

import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.MakersDto;
import co.dalicious.domain.client.entity.SparkPlusLog;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface AdminPaycheckService {
    List<MakersDto.Makers> getMakers();
    List<GroupDto.Group> getCorporations();
    void postMakersPaycheck(MultipartFile makersXlsx, MultipartFile makersPdf, PaycheckDto.MakersRequest paycheckDto) throws IOException;
    PaycheckDto.MakersResponse getMakersPaychecks(Map<String, Object> parameters);
    PaycheckDto.MakersDetail getMakersPaycheckDetail(BigInteger makersPaycheckId);
//    void updateMakersPaycheck(MultipartFile makersXlsx, MultipartFile makersPdf, PaycheckDto.MakersResponse paycheckDto) throws IOException;
    void deleteMakersPaycheck(PaycheckDto.Request request);
    void deleteMakersPaycheckById(BigInteger makersPaycheckId);
    void postMakersPaycheckAdd(BigInteger makersPaycheckId, List<PaycheckDto.PaycheckAddDto> paycheckAddDtos);
    void updateMakersPaycheckStatus(Integer status, List<BigInteger> ids);
    void postMakersMemo(BigInteger paycheckId, PaycheckDto.MemoDto memoDto);
    void postCorporationPaycheckExcel(PaycheckDto.Request request);
    void postOneCorporationPaycheckExcel(BigInteger corporationId, String yearMonth);
    PaycheckDto.CorporationMain getCorporationPaychecks(Map<String, Object> parameters);
    PaycheckDto.CorporationOrder getCorporationOrderHistory(BigInteger corporationPaycheckId);
    PaycheckDto.Invoice getCorporationInvoice(BigInteger corporationPaycheckId);
    void updateCorporationPaycheck(MultipartFile makersXlsx, MultipartFile makersPdf, PaycheckDto.CorporationResponse paycheckDto) throws IOException;
    void deleteCorporationPaycheck(PaycheckDto.Request request);
    void deleteCorporationPaycheckById(BigInteger corporationPaycheckId);
    void updateCorporationPaycheckStatus(Integer status, List<BigInteger> ids);
    void postCorporationPaycheckAdd(BigInteger corporationPaycheckId, List<PaycheckDto.PaycheckAddDto> paycheckAddDtos);
    void postCorporationMemo(BigInteger paycheckId, PaycheckDto.MemoDto memoDto);
    void postSparkplusLog(Integer log);
    List<SparkPlusLog> getSpartplusLog();
    List<MakersPaycheck> postMakersPaycheckExcel(PaycheckDto.Request request);
    void postOneMakersPaycheckExcel(BigInteger makersId, String yearMonth);

}
