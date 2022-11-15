package shop.allof.app.publicapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import io.corretto.client.core.mapper.GenericMapper;
import shop.allof.app.publicapi.dto.BannerListResponseDto;
import shop.allof.domain.banner.entity.Banner;
import shop.allof.domain.banner.enums.BannerSection;
import shop.allof.domain.banner.enums.BannerType;

@Mapper(componentModel = "spring")
public interface BannerListMapper extends GenericMapper<BannerListResponseDto, Banner> {

  @Mappings({@Mapping(source = "image.location", target = "location")})
  BannerListResponseDto toDto(Banner banner);

  default BannerType fromTypeLabel(String value) {
    System.out.println("from >> " + value);
    return BannerType.valueOf(value);
  }

  default String toTypeLabel(BannerType type) {
    System.out.println("to >> " + type);
    return type.getLabel();
  }

  default BannerSection fromSectionLabel(String value) {
    System.out.println("from >> " + value);
    return BannerSection.valueOf(value);
  }

  default String toSectionLabel(BannerSection section) {
    System.out.println("to >> " + section);
    return section.getLabel();
  }
}
