package co.dalicious.domain.logs.mapper;

import co.dalicious.domain.logs.entity.AdminLogs;
import co.dalicious.domain.logs.entity.dto.AdminLogsDto;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface AdminLogsMapper {
    @Mapping(source = "logType.code", target = "logType")
    @Mapping(source = "controllerType.code", target = "controllerType")
    @Mapping(source = "createdDateTime", target = "createdDateTime", qualifiedByName = "timestampToString")
    AdminLogsDto toDto(AdminLogs adminLogs);

    default List<AdminLogsDto> toDtos(Page<AdminLogs> adminLogs) {
        return adminLogs.getContent()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Named("timestampToString")
    default String timestampToString(Timestamp timestamp) {
        return DateUtils.format(timestamp);
    }
}
