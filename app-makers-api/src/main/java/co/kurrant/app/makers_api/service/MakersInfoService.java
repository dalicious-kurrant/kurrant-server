package co.kurrant.app.makers_api.service;


import co.dalicious.domain.file.dto.ImageWithEnumResponseDto;
import co.dalicious.domain.food.dto.OriginDto;
import co.dalicious.domain.order.dto.OrderDto;
import co.kurrant.app.makers_api.model.SecurityUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MakersInfoService {

    Object getMakersInfo(SecurityUser securityUser);
    List<OriginDto.WithId> getMakersOrigins(SecurityUser securityUser);
    void postMakersOrigins(SecurityUser securityUser, List<OriginDto> originDtos);
    void updateMakersOrigin(SecurityUser securityUser, OriginDto.WithId originDto);
    void deleteMakersOrigins(SecurityUser securityUser, OrderDto.IdList idList);
    List<ImageWithEnumResponseDto> getDocuments(SecurityUser securityUser);
    void updateDocuments(SecurityUser securityUser, MultipartFile businessLicense, MultipartFile businessPermit, MultipartFile accountCopy, List<ImageWithEnumResponseDto> images) throws IOException;
}
