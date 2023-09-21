package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.makers.AdminRecommendMakersResDto;
import co.dalicious.domain.application_form.dto.makers.NameAndAddressDto;
import co.dalicious.domain.application_form.dto.makers.RecommendMakersRequestDto;
import co.dalicious.domain.application_form.dto.makers.RecommendMakersResponseDto;
import co.dalicious.domain.application_form.entity.RecommendMakers;
import co.dalicious.domain.application_form.entity.enums.ProgressStatus;
import co.dalicious.domain.application_form.entity.enums.RecommendProgressStatus;
import org.locationtech.jts.io.ParseException;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RecommendMakersMapper {
    default RecommendMakers toRecommendMakersEntity(RecommendMakersRequestDto requestDto, BigInteger userId) throws ParseException {
        Address address = new Address(requestDto.getAddress());

        return RecommendMakers.builder()
                .userId(Collections.singletonList(userId))
                .name(requestDto.getName())
                .groupId(requestDto.getSpotId())
                .address(address)
                .phone(requestDto.getPhone())
                .progressStatus(RecommendProgressStatus.PENDING)
                .build();
    }

    default void updateUserIds(BigInteger userId, @MappingTarget RecommendMakers recommendMakers) {
        List<BigInteger> userIds = recommendMakers.getUserIds();
        if (userIds.contains(userId)) {
            userIds.remove(userId);
            recommendMakers.setUserIds(userIds);
        }
        else {
            userIds.add(userId);
            recommendMakers.setUserIds(userIds);
        }
    }

    default List<RecommendMakersResponseDto> toRecommendMakersResponseDto(List<RecommendMakers> recommendMakersList, BigInteger userId) {
        MultiValueMap<NameAndAddressDto, RecommendMakers> recommendMakersByNameMap = new LinkedMultiValueMap<>();
        for (RecommendMakers recommendMakers : recommendMakersList) {
            NameAndAddressDto nameAndAddressDto = new NameAndAddressDto(recommendMakers.getName(), recommendMakers.getAddress());
            recommendMakersByNameMap.add(nameAndAddressDto, recommendMakers);
        }

        List<RecommendMakersResponseDto> recommendMakersResponseDtos = new ArrayList<>();
        for (NameAndAddressDto nameAndAddressDto : recommendMakersByNameMap.keySet()) {
            RecommendMakersResponseDto dto = new RecommendMakersResponseDto();

            Map<String, String> getLocation = nameAndAddressDto.getAddress().getLatitudeAndLongitude();
            dto.setLatitude(getLocation.get("latitude"));
            dto.setLongitude(getLocation.get("longitude"));
            dto.setName(nameAndAddressDto.getName());

            List<RecommendMakers> recommendMakers = recommendMakersByNameMap.get(nameAndAddressDto);
            dto.setRecommendIds(recommendMakers.stream().map(RecommendMakers::getId).toList());
            dto.setIsRecommend(recommendMakers.stream().anyMatch(v -> v.getUserIds().contains(userId)));
            dto.setCount(recommendMakers.stream().flatMap(v -> v.getUserIds().stream()).collect(Collectors.toSet()).size());

            recommendMakersResponseDtos.add(dto);
        }

        return recommendMakersResponseDtos;
    }

    default List<AdminRecommendMakersResDto> toRecommendMakersResponseDto(Page<RecommendMakers> recommendMakersList, Map<BigInteger, String> groupIdAndName) {
        List<AdminRecommendMakersResDto> recommendMakersResponseDtos = new ArrayList<>();
        for (RecommendMakers recommendMakers : recommendMakersList) {
            AdminRecommendMakersResDto dto = new AdminRecommendMakersResDto();

            dto.setId(recommendMakers.getId());
            dto.setStatus(recommendMakers.getProgressStatus().getCode());
            dto.setName(recommendMakers.getName());
            dto.setAddress(recommendMakers.getAddress().addressToString());
            dto.setPhone(recommendMakers.getPhone());
            dto.setGroupName(groupIdAndName.entrySet().stream().filter(v -> v.getKey().equals(recommendMakers.getGroupId())).map(Map.Entry::getValue).findAny().orElse(null));
            dto.setCount(recommendMakers.getUserIds().size());

            recommendMakersResponseDtos.add(dto);
        }

        return recommendMakersResponseDtos;
    }

    default void updateRecommendMakersStatus(RecommendProgressStatus status, @MappingTarget RecommendMakers recommendMakers) {
        recommendMakers.setProgressStatus(status);
    }
}
