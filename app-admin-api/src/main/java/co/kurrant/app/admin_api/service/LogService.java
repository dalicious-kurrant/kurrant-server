package co.kurrant.app.admin_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.domain.logs.entity.dto.AdminLogsDto;
import co.kurrant.app.admin_api.dto.schedules.ItemPageableResponseDto;

import java.util.List;
import java.util.Map;

public interface LogService {
    ItemPageableResponseDto<List<AdminLogsDto>> getLogs(Map<String, Object> parameters, OffsetBasedPageRequest pageable);
    List<String> getDevices();
}
