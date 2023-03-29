package co.kurrant.app.admin_api.service;

import co.dalicious.domain.paycheck.dto.PaycheckDto;
import org.springframework.web.multipart.MultipartFile;

public interface PaycheckService {
    void postMakersPaycheck(MultipartFile makersXlsx, MultipartFile makersPdf, PaycheckDto.MakersRequest paycheckDto);
}
