package co.kurrant.app.admin_api.service;

import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.MakersDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public interface AdminPaycheckService {
    List<MakersDto.Makers> getMakers();
    List<GroupDto.Group> getCorporations();

    void postMakersPaycheck(MultipartFile makersXlsx, MultipartFile makersPdf, PaycheckDto.MakersRequest paycheckDto) throws IOException;
    List<PaycheckDto.MakersResponse> getMakersPaychecks();
    void updateMakersPaycheck(MultipartFile makersXlsx, MultipartFile makersPdf, PaycheckDto.MakersResponse paycheckDto) throws IOException;
    void deleteMakersPaycheck(List<BigInteger> ids);
    void updateMakersPaycheckStatus(Integer status, List<BigInteger> ids);

    void postCorporationPaycheck(MultipartFile corporationXlsx, MultipartFile corporationPdf, PaycheckDto.CorporationRequest paycheckDto) throws IOException;
    List<PaycheckDto.CorporationResponse> getCorporationPaychecks();
    void updateCorporationPaycheck(MultipartFile makersXlsx, MultipartFile makersPdf, PaycheckDto.CorporationResponse paycheckDto) throws IOException;
    void deleteCorporationPaycheck(List<BigInteger> ids);
    void updateCorporationPaycheckStatus(Integer status, List<BigInteger> ids);
}
