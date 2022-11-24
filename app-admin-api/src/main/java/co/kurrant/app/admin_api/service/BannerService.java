package co.kurrant.app.admin_api.service;

import java.math.BigInteger;

import org.springframework.data.domain.Pageable;
import co.dalicious.client.core.dto.response.CreateResponseDto;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.client.core.dto.response.SuccessResponseDto;
import co.kurrant.app.admin_api.dto.BannerCreateRequestDto;
import co.kurrant.app.admin_api.dto.BannerDetailResponseDto;
import co.kurrant.app.admin_api.dto.BannerListRequestDto;
import co.kurrant.app.admin_api.dto.BannerListResponseDto;
import co.kurrant.app.admin_api.dto.BannerUpdateRequestDto;

public interface BannerService {
  ListItemResponseDto<BannerListResponseDto> getList(BannerListRequestDto dto, Pageable pageable);

  BannerDetailResponseDto getOne(BigInteger id);

  CreateResponseDto<String> createOne(BannerCreateRequestDto body);

  SuccessResponseDto updateOne(BigInteger id, BannerUpdateRequestDto body);

  SuccessResponseDto deleteOne(BigInteger id);
}
