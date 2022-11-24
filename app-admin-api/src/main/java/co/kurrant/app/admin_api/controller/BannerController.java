package co.kurrant.app.admin_api.controller;

import java.math.BigInteger;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import co.kurrant.app.admin_api.service.BannerService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import co.dalicious.client.core.dto.response.CreateResponseDto;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.client.core.dto.response.SuccessResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import co.kurrant.app.admin_api.dto.BannerCreateRequestDto;
import co.kurrant.app.admin_api.dto.BannerDetailResponseDto;
import co.kurrant.app.admin_api.dto.BannerListRequestDto;
import co.kurrant.app.admin_api.dto.BannerListResponseDto;
import co.kurrant.app.admin_api.dto.BannerUpdateRequestDto;

@Tag(name = "1. Banner")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/banners")
@RestController
public class BannerController {
  private final BannerService bannerService;

  @Operation(summary = "목록조회", description = "배너 목록조회를 수행한다.")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("")

  public ListItemResponseDto<BannerListResponseDto> getList(HttpServletRequest request,
                                                            @Parameter(name = "쿼리정보", description = "",
          required = false) @Valid @ModelAttribute BannerListRequestDto dto,
                                                            @PageableDefault(size = 100, sort = "createdDateTime",
          direction = Direction.DESC) Pageable pageable) {
    return bannerService.getList(dto, pageable);
  }

  @Operation(summary = "생성요청", description = "배너 생성요청을 수행한다.")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("")
  public CreateResponseDto<String> createOne(@Valid @RequestBody BannerCreateRequestDto body) {
    return bannerService.createOne(body);
  }

  @Operation(summary = "상세조회", description = "배너 상세조회를 수행한다.")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{bannerId}")
  public BannerDetailResponseDto getOne(@PathVariable BigInteger bannerId) {
    return bannerService.getOne(bannerId);
  }

  @Operation(summary = "수정요청", description = "배너 수정요청을 수행한다.")
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{bannerId}")
  public SuccessResponseDto updateOne(@PathVariable BigInteger bannerId,
      @Valid @RequestBody BannerUpdateRequestDto body) {
    return bannerService.updateOne(bannerId, body);
  }

  @Operation(summary = "삭제요청", description = "배너 삭제요청을 수행한다.")
  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping("/{bannerId}")
  public SuccessResponseDto deleteOne(@PathVariable BigInteger bannerId) {
    return bannerService.deleteOne(bannerId);
  }
}
