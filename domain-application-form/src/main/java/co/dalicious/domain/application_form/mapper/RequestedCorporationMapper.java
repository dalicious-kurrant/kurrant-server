package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.application_form.dto.corporation.CorporationRequestAtHomepageDto;
import co.dalicious.domain.application_form.entity.RequestedCorporation;
import co.dalicious.domain.application_form.entity.enums.ProgressStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
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

}
