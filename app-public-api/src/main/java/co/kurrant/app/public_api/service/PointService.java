package co.kurrant.app.public_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.domain.user.dto.PointRequestDto;

import java.util.List;

public interface PointService {
    List<PointRequestDto> findAllPointLogs(Integer condition, Integer limit, Integer page, OffsetBasedPageRequest pageable);
}
