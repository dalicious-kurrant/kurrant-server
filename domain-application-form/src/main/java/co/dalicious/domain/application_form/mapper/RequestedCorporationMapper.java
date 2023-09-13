package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.application_form.dto.StatusUpdateDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationRequestAtHomepageDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationRequestReqDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationRequestResDto;
import co.dalicious.domain.application_form.dto.makers.MakersRequestedReqDto;
import co.dalicious.domain.application_form.dto.makers.MakersRequestedResDto;
import co.dalicious.domain.application_form.entity.RequestedCorporation;
import co.dalicious.domain.application_form.entity.RequestedMakers;
import co.dalicious.domain.application_form.entity.enums.ProgressStatus;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface RequestedCorporationMapper {

    default RequestedCorporation toRequestedCorporationEntity(CorporationRequestAtHomepageDto request) {
        return RequestedCorporation.builder()
                .username(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .memo(request.getMemo())
                .progressStatus(ProgressStatus.APPLY)
                .build();
    }

    default RequestedCorporation toRequestedCorporationEntity(CorporationRequestReqDto request) {
        return RequestedCorporation.builder()
                .username(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .memo(request.getMemo())
                .progressStatus(ProgressStatus.ofCode(request.getProgressStatus()))
                .build();
    }

    default List<CorporationRequestResDto> toCorporationRequestedResDtoList(Page<RequestedCorporation> requestedCorporations) {
        return requestedCorporations.stream()
                .map(this::toCorporationRequestedResDto)
                .toList();
    }

    @Mapping(source = "username", target = "name")
    @Mapping(target = "createDate", expression = "java(DateUtils.toISOLocalDate(requestedCorporation.getCreatedDateTime()))")
    @Mapping(source = "progressStatus.code", target = "progressStatus")
    CorporationRequestResDto toCorporationRequestedResDto(RequestedCorporation requestedCorporation);

    default void updateRequestedCorporationStatus(StatusUpdateDto dto, @MappingTarget RequestedCorporation requestedCorporation) {
        requestedCorporation.setProgressStatus(ProgressStatus.ofCode(dto.getStatus()));
    }

}
