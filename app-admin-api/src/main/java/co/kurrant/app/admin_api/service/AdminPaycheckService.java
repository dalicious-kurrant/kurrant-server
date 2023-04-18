package co.kurrant.app.admin_api.service;

import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.MakersDto;
import co.dalicious.domain.client.entity.SparkPlusLog;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface AdminPaycheckService {
    List<MakersDto.Makers> getMakers();
    List<GroupDto.Group> getCorporations();

    void postMakersPaycheck(MultipartFile makersXlsx, MultipartFile makersPdf, PaycheckDto.MakersRequest paycheckDto) throws IOException;
    List<PaycheckDto.MakersResponse> getMakersPaychecks(Map<String, Object> parameters);
    PaycheckDto.MakersDetail getMakersPaycheckDetail(BigInteger makersPaycheckId);
    void updateMakersPaycheck(MultipartFile makersXlsx, MultipartFile makersPdf, PaycheckDto.MakersResponse paycheckDto) throws IOException;
//    void deleteMakersPaycheck(List<BigInteger> ids);
    void postPaycheckAdd(BigInteger makersPaycheckId, List<PaycheckDto.PaycheckAddDto> paycheckAddDtos);
    void updateMakersPaycheckStatus(Integer status, List<BigInteger> ids);

    void postCorporationPaycheck(MultipartFile corporationXlsx, MultipartFile corporationPdf, PaycheckDto.CorporationRequest paycheckDto) throws IOException;
    List<PaycheckDto.CorporationResponse> getCorporationPaychecks();
    void updateCorporationPaycheck(MultipartFile makersXlsx, MultipartFile makersPdf, PaycheckDto.CorporationResponse paycheckDto) throws IOException;
    void deleteCorporationPaycheck(List<BigInteger> ids);
    void updateCorporationPaycheckStatus(Integer status, List<BigInteger> ids);

    void postSparkplusLog(Integer log);
    List<SparkPlusLog> getSpartplusLog();
    List<MakersPaycheck> postMakersPaycheckExcel();

}
