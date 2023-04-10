package co.kurrant.app.public_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.domain.user.dto.PointResponseDto;
import co.kurrant.app.public_api.model.SecurityUser;

public interface PointService {
    ItemPageableResponseDto<PointResponseDto> findAllPointLogs(SecurityUser securityUser, Integer condition, OffsetBasedPageRequest pageable); //, Integer limit, Integer page
}
