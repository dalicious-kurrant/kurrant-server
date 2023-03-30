package co.kurrant.app.public_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.domain.user.dto.PointRequestDto;
import co.kurrant.app.public_api.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
    @Override
    @Transactional(readOnly = true)
    public List<PointRequestDto> findAllPointLogs(Integer condition, Integer limit, Integer page, OffsetBasedPageRequest pageable) {

        return null;
    }
}
