package co.kurrant.app.public_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.domain.user.dto.PointRequestDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.util.List;

public interface PointService {
    List<PointRequestDto> findAllPointLogs(SecurityUser securityUser, Integer condition, Integer limit, Integer page, OffsetBasedPageRequest pageable);
}
