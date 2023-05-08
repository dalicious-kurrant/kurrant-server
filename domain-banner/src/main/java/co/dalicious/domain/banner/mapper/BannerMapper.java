package co.dalicious.domain.banner.mapper;

import co.dalicious.domain.banner.entity.Banner;
import co.dalicious.domain.banner.entity.dto.BannerDto;
import co.dalicious.domain.banner.enums.BannerSection;
import co.dalicious.domain.banner.enums.BannerType;
import co.dalicious.domain.file.dto.ImageResponseDto;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", imports = {DateUtils.class})
public interface BannerMapper {
    default Banner toEntity(BannerDto.Request bannerDto, ImageResponseDto imageDto) {
        return Banner.builder()
                .startDate(DateUtils.stringToDate(bannerDto.getStartDate()))
                .endDate(DateUtils.stringToDate(bannerDto.getEndDate()))
                .type(BannerType.ofCode(bannerDto.getType()))
                .section(BannerSection.ofCode(bannerDto.getSection()))
                .image(new Image(imageDto))
                .build();
    }
}
