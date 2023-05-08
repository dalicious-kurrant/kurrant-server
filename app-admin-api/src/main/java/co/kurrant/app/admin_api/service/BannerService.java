package co.kurrant.app.admin_api.service;

import co.dalicious.domain.banner.entity.dto.BannerDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface BannerService {
    void postBanner(BannerDto.Request request, MultipartFile multipartFile) throws IOException;
}
