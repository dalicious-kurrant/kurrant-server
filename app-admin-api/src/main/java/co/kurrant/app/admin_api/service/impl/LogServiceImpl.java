package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.enums.ControllerType;
import co.dalicious.domain.logs.entity.AdminLogs;
import co.dalicious.domain.logs.entity.dto.AdminLogsDto;
import co.dalicious.domain.logs.entity.enums.LogType;
import co.dalicious.domain.logs.mapper.AdminLogsMapper;
import co.dalicious.domain.logs.repository.QAdminLogsRepository;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.StringUtils;
import co.kurrant.app.admin_api.dto.schedules.ItemPageableResponseDto;
import co.kurrant.app.admin_api.service.LogService;
import exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
    private final QAdminLogsRepository qAdminLogsRepository;
    private final AdminLogsMapper adminLogsMapper;

    @Override
    public ItemPageableResponseDto<List<AdminLogsDto>> getLogs(Map<String, Object> parameters, OffsetBasedPageRequest pageable) {
        List<Integer> logType = !parameters.containsKey("logType") || parameters.get("logType").equals("") ? null : StringUtils.parseIntegerList((String) parameters.get("logType"));
        List<Integer> controllerType = !parameters.containsKey("controllerType") || parameters.get("controllerType").equals("") ? null : StringUtils.parseIntegerList((String) parameters.get("controllerType"));
        Integer limit = !parameters.containsKey("limit") || parameters.get("limit") == null ? null : Integer.parseInt(String.valueOf(parameters.get("limit")));
        Integer page = !parameters.containsKey("page") || parameters.get("page") == null ? null : Integer.parseInt(String.valueOf(parameters.get("page")));
        LocalDateTime startDate = !parameters.containsKey("startDate") || parameters.get("startDate") == null ? null : DateUtils.stringToLocalDateTime(String.valueOf(parameters.get("startDate")));
        LocalDateTime endDate = !parameters.containsKey("endDate") || parameters.get("endDate") == null ? null : DateUtils.stringToLocalDateTime(String.valueOf(parameters.get("endDate")));

        if(limit == null || page == null) {
            String missingParams = "";
            if(limit == null) missingParams += "limit, ";
            if(page == null) missingParams += "page, ";

            // Removes the last comma and space
            missingParams = missingParams.substring(0, missingParams.length() - 2);

            throw new CustomException(HttpStatus.BAD_REQUEST, "CE400003",
                    missingParams + "에 잘못된 파라미터 값을 넣으셨습니다.");
        }

        Page<AdminLogs> adminLogs =  qAdminLogsRepository.findAllByFilter(LogType.ofCodes(logType), ControllerType.ofCodes(controllerType), startDate, endDate, limit, page, pageable);
        return ItemPageableResponseDto.<List<AdminLogsDto>>builder()
                .items(adminLogsMapper.toDtos(adminLogs))
                .limit(pageable.getPageSize()).total((long) adminLogs.getTotalPages())
                .count(adminLogs.getNumberOfElements()).build();
    }
}
