//package co.kurrant.app.public_api.controller;
//
//import java.util.Map;
//import javax.servlet.http.HttpServletRequest;
//
//import co.kurrant.app.public_api.dto.BannerListRequestDto;
//import co.kurrant.app.public_api.dto.BannerListResponseDto;
//import co.kurrant.app.public_api.service.BannerService;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort.Direction;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestController;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.json.JsonMapper;
//import co.dalicious.client.core.dto.response.ListItemResponseDto;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//
//@Tag(name = "1. Banner")
//@RequiredArgsConstructor
//@RequestMapping(value = "/v1/banners")
//@RestController
//public class BannerController {
//  private final BannerService bannerService;
//
//  @Operation(summary = "목록조회", description = "모든 Banner의 목록을 조회한다.")
//  @ResponseStatus(HttpStatus.OK)
//  @GetMapping("")
//  public ListItemResponseDto<BannerListResponseDto> getList(HttpServletRequest request,
//                                                            @Parameter(name = "쿼리정보", description = "",
//          required = false) @RequestParam Map<String, String> params,
//                                                            @PageableDefault(size = 100, sort = "createdDateTime",
//          direction = Direction.DESC) Pageable pageable) {
//
//    JsonMapper mapper = JsonMapper.builder()
//        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build();
//
//    return bannerService.getAllBanners(mapper.convertValue(params, BannerListRequestDto.class),
//        pageable);
//  }
//
//}
