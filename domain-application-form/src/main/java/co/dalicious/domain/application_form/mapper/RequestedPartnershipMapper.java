package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.application_form.dto.StatusUpdateDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationRequestAtHomepageDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationRequestReqDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationRequestResDto;
import co.dalicious.domain.application_form.dto.makers.MakersRequestAtHomepageDto;
import co.dalicious.domain.application_form.dto.makers.MakersRequestedReqDto;
import co.dalicious.domain.application_form.dto.makers.MakersRequestedResDto;
import co.dalicious.domain.application_form.entity.RequestedCorporation;
import co.dalicious.domain.application_form.entity.RequestedMakers;
import co.dalicious.domain.application_form.entity.RequestedPartnership;
import co.dalicious.domain.application_form.entity.enums.HomepageRequestedType;
import co.dalicious.domain.application_form.entity.enums.ProgressStatus;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface RequestedPartnershipMapper {

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

    default void updateRequestedCorporationStatus(StatusUpdateDto dto, @MappingTarget RequestedPartnership requestedCorporation) {
        requestedCorporation.setProgressStatus(ProgressStatus.ofCode(dto.getStatus()));
    }

    default RequestedMakers toRequestedMakersEntity(MakersRequestAtHomepageDto request) {
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

    default RequestedMakers toRequestedMakersEntity(MakersRequestedReqDto request) {
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

    default void updateRequestedMakersStatus(StatusUpdateDto dto, @MappingTarget RequestedPartnership requestedMakers) {
        requestedMakers.setProgressStatus(ProgressStatus.ofCode(dto.getStatus()));
    }
}
