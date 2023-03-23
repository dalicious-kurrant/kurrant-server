package co.kurrant.app.makers_api.service;


import co.dalicious.domain.food.dto.OriginDto;
import co.kurrant.app.makers_api.model.SecurityUser;

import java.util.List;

public interface MakersInfoService {

    Object getMakersInfo(SecurityUser securityUser);
    List<OriginDto.WithId> getMakersOrigins(SecurityUser securityUser);
    void postMakersOrigins(SecurityUser securityUser, List<OriginDto> originDtos);
    void updateMakersOrigin(SecurityUser securityUser, OriginDto.WithId originDto);
}
