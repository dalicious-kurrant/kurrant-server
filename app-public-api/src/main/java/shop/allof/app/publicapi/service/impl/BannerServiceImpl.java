package shop.allof.app.publicapi.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.corretto.client.core.dto.response.ListItemResponseDto;
import lombok.RequiredArgsConstructor;
import shop.allof.app.publicapi.dto.BannerListRequestDto;
import shop.allof.app.publicapi.dto.BannerListResponseDto;
import shop.allof.app.publicapi.mapper.BannerListMapper;
import shop.allof.app.publicapi.repository.BannerRepository;
import shop.allof.app.publicapi.service.BannerService;
import shop.allof.domain.banner.entity.Banner;

@Service
@RequiredArgsConstructor
@Transactional
public class BannerServiceImpl implements BannerService {
  private final BannerListMapper mapper;

  private final BannerRepository bannerRepository;

  @Override
  public ListItemResponseDto<BannerListResponseDto> getAllBanners(BannerListRequestDto dto,
      Pageable pageable) {

    Page<Banner> allBanners = bannerRepository.findAll(pageable);

    List<BannerListResponseDto> items =
        allBanners.get().map((banner) -> mapper.toDto(banner)).collect(Collectors.toList());

    return ListItemResponseDto.<BannerListResponseDto>builder().total(allBanners.getTotalElements())
        .count(allBanners.getNumberOfElements()).limit(pageable.getPageSize())
        .offset(pageable.getOffset()).items(items).build();
  }

}
