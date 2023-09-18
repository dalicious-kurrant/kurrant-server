package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.application_form.dto.StatusUpdateDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationRequestAtHomepageDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationRequestReqDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationRequestResDto;
import co.dalicious.domain.application_form.dto.makers.MakersRequestAtHomepageDto;
import co.dalicious.domain.application_form.dto.makers.MakersRequestedReqDto;
import co.dalicious.domain.application_form.dto.makers.MakersRequestedResDto;
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

    default RequestedPartnership toRequestedCorporationEntity(CorporationRequestAtHomepageDto request) {
        return RequestedPartnership.builder()
                .username(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .memo(request.getMemo())
                .progressStatus(ProgressStatus.APPLY)
                .requestedType(HomepageRequestedType.CORPORATION)
                .build();
    }

    default RequestedPartnership toRequestedCorporationEntity(CorporationRequestReqDto request) {
        return RequestedPartnership.builder()
                .username(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .memo(request.getMemo())
                .progressStatus(ProgressStatus.ofCode(request.getProgressStatus()))
                .requestedType(HomepageRequestedType.CORPORATION)
                .build();
    }

    default List<CorporationRequestResDto> toCorporationRequestedResDtoList(Page<RequestedPartnership> requestedCorporations) {
        return requestedCorporations.stream()
                .map(this::toCorporationRequestedResDto)
                .toList();
    }

    @Mapping(source = "username", target = "name")
    @Mapping(target = "createDate", expression = "java(DateUtils.toISOLocalDate(requestedCorporation.getCreatedDateTime()))")
    @Mapping(source = "progressStatus.code", target = "progressStatus")
    CorporationRequestResDto toCorporationRequestedResDto(RequestedPartnership requestedCorporation);

    default void updateRequestedCorporationStatus(StatusUpdateDto dto, @MappingTarget RequestedPartnership requestedCorporation) {
        requestedCorporation.setProgressStatus(ProgressStatus.ofCode(dto.getStatus()));
    }

    default RequestedPartnership toRequestedMakersEntity(MakersRequestAtHomepageDto request) {
        return RequestedPartnership.builder()
                .username(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .memo(request.getMemo())
                .makersName(request.getMakersName())
                .mainProduct(request.getMainProduct())
                .progressStatus(ProgressStatus.APPLY)
                .requestedType(HomepageRequestedType.MAKERS)
                .build();
    }

    default RequestedPartnership toRequestedMakersEntity(MakersRequestedReqDto request) {
        return RequestedPartnership.builder()
                .username(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .memo(request.getMemo())
                .makersName(request.getMakersName())
                .mainProduct(request.getMainProduct())
                .progressStatus(ProgressStatus.ofCode(request.getProgressStatus()))
                .requestedType(HomepageRequestedType.MAKERS)
                .build();
    }

    default List<MakersRequestedResDto> toMakersRequestedResDtoList(Page<RequestedPartnership> requestedMakersList) {
        return requestedMakersList.stream()
                .map(this::toMakersRequestedResDto)
                .toList();
    }

    @Mapping(source = "username", target = "name")
    @Mapping(target = "createDate", expression = "java(DateUtils.toISOLocalDate(requestedMakers.getCreatedDateTime()))")
    @Mapping(source = "progressStatus.code", target = "progressStatus")
    MakersRequestedResDto toMakersRequestedResDto(RequestedPartnership requestedMakers);

    default void updateRequestedMakersStatus(StatusUpdateDto dto, @MappingTarget RequestedPartnership requestedMakers) {
        requestedMakers.setProgressStatus(ProgressStatus.ofCode(dto.getStatus()));
    }
}
