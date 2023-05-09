package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.banner.entity.dto.BannerDto;
import co.kurrant.app.admin_api.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/banners")
public class BannerController {
    private final BannerService bannerService;
    @Operation(summary = "배너 등록", description = "배너를 등록한다")
    @PostMapping("/")
    public ResponseMessage postBanner(@RequestPart BannerDto.Request request, @RequestPart MultipartFile image) throws IOException {
        bannerService.postBanner(request, image);
        return ResponseMessage.builder()
                .message("배너 등록에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "배너 전체 조회", description = "등록된 배너의 전체 목록을 조회한다.")
    @GetMapping("/")
    public ResponseMessage getBanners() {
        return ResponseMessage.builder()
                .message("배너 전체 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "배너 상세 조회", description = "배너의 상세 정보를 조회한다.")
    @GetMapping("/{bannerId}")
    public ResponseMessage getBanner(@PathVariable BigInteger bannerId) {
        return ResponseMessage.builder()
                .message("배너 상세 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "배너 상세 조회", description = "배너의 상세 정보를 조회한다.")
    @PatchMapping("/{bannerId}")
    public ResponseMessage updateBanner(@PathVariable BigInteger bannerId) {
        return ResponseMessage.builder()
                .message("배너 수정에 성공하였습니다.")
                .build();
    }
}
