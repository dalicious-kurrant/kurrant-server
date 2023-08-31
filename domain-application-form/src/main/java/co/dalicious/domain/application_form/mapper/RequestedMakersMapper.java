package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.application_form.dto.makers.MakersRequestAtHomepageDto;
import co.dalicious.domain.application_form.dto.makers.MakersRequestedReqDto;
import co.dalicious.domain.application_form.dto.makers.MakersRequestedResDto;
import co.dalicious.domain.application_form.dto.makers.MakersRequestedStatusUpdateDto;
import co.dalicious.domain.application_form.entity.RequestedMakers;
import co.dalicious.domain.application_form.entity.enums.ProgressStatus;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring" , imports = DateUtils.class)
public interface RequestedMakersMapper {

    default RequestedMakers toRequestedCorporationEntity(MakersRequestAtHomepageDto request) {
        return RequestedMakers.builder()
                .username(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .memo(request.getMemo())
                .makersName(request.getMakersName())
                .mainProduct(request.getMainProduct())
                .progressStatus(ProgressStatus.APPLY)
                .build();
    }

    default RequestedMakers toRequestedCorporationEntity(MakersRequestedReqDto request) {
        return RequestedMakers.builder()
                .username(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .memo(request.getMemo())
                .makersName(request.getMakersName())
                .mainProduct(request.getMainProduct())
                .progressStatus(ProgressStatus.ofCode(request.getProgressStatus()))
                .build();
    }

    default List<MakersRequestedResDto> toMakersRequestedResDtoList(Page<RequestedMakers> requestedMakersList) {
        return requestedMakersList.stream()
                .map(this::toMakersRequestedResDto)
                .toList();
    }

    @Mapping(source = "username", target = "name")
    @Mapping(target = "createDate", expression = "java(DateUtils.toISOLocalDate(requestedMakers.getCreatedDateTime()))")
    @Mapping(source = "progressStatus.code", target = "progressStatus")
    MakersRequestedResDto toMakersRequestedResDto(RequestedMakers requestedMakers);

    default void updateRequestedMakersStatus(MakersRequestedStatusUpdateDto dto, @MappingTarget RequestedMakers requestedMakers) {
        requestedMakers.setProgressStatus(ProgressStatus.ofCode(dto.getStatus()));
    }
}
