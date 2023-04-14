package co.kurrant.app.public_api.mapper.user;

import co.dalicious.domain.user.dto.ProviderEmailDto;
import co.dalicious.domain.user.entity.ProviderEmail;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.PushCondition;
import co.kurrant.app.public_api.dto.user.MarketingAlarmResponseDto;
import co.kurrant.app.public_api.dto.user.UserPersonalInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
@Transactional
public interface UserPersonalInfoMapper {
    @Mapping(target = "avatar", source = "avatar.location")
    @Mapping(target = "gourmetType", source = "gourmetType.gourmetType")
    @Mapping(target = "providerEmails", source = "providerEmails", qualifiedByName = "providerEmailsToDto")
    UserPersonalInfoDto toDto(User user);

    @Named("providerEmailsToDto")
    default List<ProviderEmailDto> providerEmailsToDto(List<ProviderEmail> providerEmails) {
        List<ProviderEmailDto> providerEmailDtos = new ArrayList<>();
        for (ProviderEmail providerEmail : providerEmails) {
            providerEmailDtos.add(ProviderEmailDto.builder()
                    .email(providerEmail.getEmail())
                    .provider(providerEmail.getProvider().getProvider())
                    .build());
        }
        return providerEmailDtos;
    }

    default MarketingAlarmResponseDto toMarketingAlarmResponseDto(List<PushCondition> userPushCondition, PushCondition pushCondition) {
        MarketingAlarmResponseDto responseDto = new MarketingAlarmResponseDto();

        if(userPushCondition == null || userPushCondition.isEmpty() || !userPushCondition.contains(pushCondition)) {
            responseDto.setCode(pushCondition.getCode());
            responseDto.setCondition(pushCondition.getCondition());
            responseDto.setIsActive(false);
        }
        else {
            responseDto.setCode(pushCondition.getCode());
            responseDto.setCondition(pushCondition.getCondition());
            responseDto.setIsActive(true);
        }

        return responseDto;
    }
}
