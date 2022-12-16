package co.kurrant.app.public_api.dto.user;

import co.dalicious.domain.user.dto.ProviderEmailDto;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "아이디 찾기 응답 DTO")
@Getter
@NoArgsConstructor
public class FindIdResponseDto {
    private String email;
    private List<ProviderEmailDto> connectedSns;
    private String recentLoginDateTime;

    @Builder
    public FindIdResponseDto(String email, List<ProviderEmailDto> connectedSns, String recentLoginDateTime) {
        this.email = email;
        this.connectedSns = connectedSns;
        this.recentLoginDateTime = recentLoginDateTime;
    }
}
