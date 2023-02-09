//package co.kurrant.app.public_api.service.impl;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import co.kurrant.app.public_api.dto.BannerListRequestDto;
//import co.kurrant.app.public_api.dto.BannerListResponseDto;
//import co.kurrant.app.public_api.mapper.BannerListMapper;
//import co.kurrant.app.public_api.repository.BannerRepository;
//import co.kurrant.app.public_api.service.BannerService;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import co.dalicious.client.core.dto.response.ListItemResponseDto;
//import lombok.RequiredArgsConstructor;
//import co.dalicious.domain.banner.entity.Banner;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class BannerServiceImpl implements BannerService {
//  private final BannerListMapper mapper;
//
//  private final BannerRepository bannerRepository;
//
//  @Override
//  public ListItemResponseDto<BannerListResponseDto> getAllBanners(BannerListRequestDto dto,
//                                                                  Pageable pageable) {
//
//    Page<Banner> allBanners = bannerRepository.findAll(pageable);
//
//    List<BannerListResponseDto> items =
//        allBanners.get().map((banner) -> mapper.toDto(banner)).collect(Collectors.toList());
//
//    return ListItemResponseDto.<BannerListResponseDto>builder().total(allBanners.getTotalElements())
//        .count(allBanners.getNumberOfElements()).limit(pageable.getPageSize())
//        .offset(pageable.getOffset()).items(items).build();
//  }
//
//}
