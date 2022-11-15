package shop.allof.app.publicapi.service;

import org.springframework.data.domain.Pageable;
import io.corretto.client.core.dto.response.ListItemResponseDto;
import shop.allof.app.publicapi.dto.BannerListRequestDto;
import shop.allof.app.publicapi.dto.BannerListResponseDto;

public interface BannerService {
  ListItemResponseDto<BannerListResponseDto> getAllBanners(BannerListRequestDto dto,
      Pageable pageable);

}
