package co.kurrant.app.admin_api.service.impl;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import co.kurrant.app.admin_api.service.BannerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.dalicious.client.core.dto.response.CreateResponseDto;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.client.core.dto.response.SuccessResponseDto;
import exception.ApiException;
import exception.ExceptionEnum;
import co.dalicious.domain.file.entity.embeddable.Image;
import lombok.RequiredArgsConstructor;
import co.kurrant.app.admin_api.dto.BannerCreateRequestDto;
import co.kurrant.app.admin_api.dto.BannerDetailResponseDto;
import co.kurrant.app.admin_api.dto.BannerListRequestDto;
import co.kurrant.app.admin_api.dto.BannerListResponseDto;
import co.kurrant.app.admin_api.dto.BannerUpdateRequestDto;
import co.kurrant.app.admin_api.repository.BannerRepository;
import co.dalicious.domain.banner.entity.Banner;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {
  private final BannerRepository bannerRepository;

  @Override
  @Transactional(readOnly = true)
  public ListItemResponseDto<BannerListResponseDto> getList(BannerListRequestDto dto,
                                                            Pageable pageable) {

    Page<Banner> allBanners = bannerRepository.findAll(pageable);

    List<BannerListResponseDto> items = allBanners.get()
        .map((banner) -> BannerListResponseDto.builder().id(banner.getId().toString())
            .type(banner.getType().getLabel()).location(banner.getImage().getLocation())
            .moveTo(banner.getMoveTo()).section(banner.getSection().getLabel()).build())
        .collect(Collectors.toList());

    return ListItemResponseDto.<BannerListResponseDto>builder().total(allBanners.getTotalElements())
        .count(allBanners.getNumberOfElements()).limit(pageable.getPageSize())
        .offset(pageable.getOffset()).items(items).build();
  }

  @Override
  @Transactional
  public CreateResponseDto<String> createOne(BannerCreateRequestDto body) {
    Banner banner =
        Banner.builder().type(body.getType()).section(body.getSection()).moveTo(body.getMoveTo())
            .image(Image.builder().key(body.getImage().getKey())
                .location(body.getImage().getLocation()).filename(body.getImage().getFilename())
                .build())
            .build();

    bannerRepository.save(banner);

    return CreateResponseDto.<String>builder().id(banner.getId().toString()).build();
  }

  @Override
  @Transactional
  public SuccessResponseDto updateOne(BigInteger bannerId, BannerUpdateRequestDto body) {
    Banner banner = bannerRepository.findById(bannerId)
        .orElseThrow(() -> new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND));
    //
    // banner.updateBanner(body.getType(), body.getSection(), body.getMoveTo(),
    // Image.builder().filename(body.getImageCreateRequestDto().getFilename())
    // .location(body.getImageCreateRequestDto().getLocation())
    // .key(body.getImageCreateRequestDto().getKey()).build());

    bannerRepository.save(banner);

    return SuccessResponseDto.builder().success(true).build();
  }

  @Override
  @Transactional
  public SuccessResponseDto deleteOne(BigInteger bannerId) {
    Banner banner = bannerRepository.findById(bannerId)
        .orElseThrow(() -> new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND));

    bannerRepository.delete(banner);

    return SuccessResponseDto.builder().success(true).build();
  }

  @Override
  public BannerDetailResponseDto getOne(BigInteger id) {
    // TODO Auto-generated method stub
    return null;
  }

}
