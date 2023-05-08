package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.banner.entity.Banner;
import co.dalicious.domain.banner.entity.dto.BannerDto;
import co.dalicious.domain.banner.mapper.BannerMapper;
import co.dalicious.domain.banner.repository.BannerRepository;
import co.dalicious.domain.file.dto.ImageResponseDto;
import co.dalicious.domain.file.service.ImageService;
import co.kurrant.app.admin_api.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {
    private final ImageService imageService;
    private final BannerMapper bannerMapper;
    private final BannerRepository bannerRepository;

    @Override
    public void postBanner(BannerDto.Request request, MultipartFile multipartFile) throws IOException {
        ImageResponseDto imageResponseDto = imageService.upload(multipartFile, "banner");
        Banner banner = bannerMapper.toEntity(request, imageResponseDto);
        bannerRepository.save(banner);
    }
}
