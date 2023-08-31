package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.application_form.dto.makers.MakersRequestAtHomepageDto;
import co.dalicious.domain.application_form.entity.RequestedMakers;
import co.dalicious.domain.application_form.entity.enums.ProgressStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
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
}
