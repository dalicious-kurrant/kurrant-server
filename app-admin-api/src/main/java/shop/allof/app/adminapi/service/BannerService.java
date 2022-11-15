package shop.allof.app.adminapi.service;

import java.math.BigInteger;
import org.springframework.data.domain.Pageable;
import io.corretto.client.core.dto.response.CreateResponseDto;
import io.corretto.client.core.dto.response.ListItemResponseDto;
import io.corretto.client.core.dto.response.SuccessResponseDto;
import shop.allof.app.adminapi.dto.BannerCreateRequestDto;
import shop.allof.app.adminapi.dto.BannerDetailResponseDto;
import shop.allof.app.adminapi.dto.BannerListRequestDto;
import shop.allof.app.adminapi.dto.BannerListResponseDto;
import shop.allof.app.adminapi.dto.BannerUpdateRequestDto;

public interface BannerService {
  ListItemResponseDto<BannerListResponseDto> getList(BannerListRequestDto dto, Pageable pageable);

  BannerDetailResponseDto getOne(BigInteger id);

  CreateResponseDto<String> createOne(BannerCreateRequestDto body);

  SuccessResponseDto updateOne(BigInteger id, BannerUpdateRequestDto body);

  SuccessResponseDto deleteOne(BigInteger id);
}
