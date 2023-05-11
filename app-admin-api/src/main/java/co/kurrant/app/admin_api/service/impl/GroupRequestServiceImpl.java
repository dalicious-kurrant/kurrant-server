package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.dto.mySpotZone.filter.FilterDto;
import co.kurrant.app.admin_api.service.GroupRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GroupRequestServiceImpl implements GroupRequestService {
    @Override
    public FilterDto getAllListForFilter(Map<String, Object> parameters) {
        // 시/도, 군/구, 동/읍/리 별로 필터. - 군/구, 동/읍/리는 다중 필터 가능
        String city = parameters.get("city") == null || !parameters.containsKey("city") ? null : String.valueOf(parameters.get("city"));
        List<String> county = parameters.get("county") == null || !parameters.containsKey("county") ? null : Collections.singletonList(String.valueOf(parameters.get("county")));
        List<String> village = parameters.get("village") == null || !parameters.containsKey("village") ? null : Collections.singletonList(String.valueOf(parameters.get("village")));



        return null;
    }
}
