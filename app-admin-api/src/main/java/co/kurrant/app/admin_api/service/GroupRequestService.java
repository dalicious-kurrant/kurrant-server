package co.kurrant.app.admin_api.service;

import co.dalicious.domain.client.dto.mySpotZone.filter.FilterDto;

import java.util.Map;

public interface GroupRequestService {
    FilterDto getAllListForFilter(Map<String, Object> parameters);
}
