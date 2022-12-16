package co.dalicious.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ProviderEmailDto {
    String provider;
    String email;

    @Builder
    public ProviderEmailDto(String provider, String email) {
        this.provider = provider;
        this.email = email;
    }
}
