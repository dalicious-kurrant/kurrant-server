package co.kurrant.app.public_api.mapper.user;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.user.dto.ProviderEmailDto;
import co.dalicious.domain.user.entity.ProviderEmail;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.user.UserPersonalInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
@Transactional
public interface UserPersonalInfoMapper extends GenericMapper<UserPersonalInfoDto, User> {
    @Override
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
}
