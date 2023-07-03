package co.dalicious.domain.banner.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum BannerType {
  BUY("구매", 1),
  SELL("판매", 2);

  private final String label;
  private final Integer code;

  BannerType(String label, Integer code) {
    this.label = label;
    this.code = code;
  }

  @JsonCreator
  public static BannerType from(String val) {
    for (BannerType type : BannerType.values()) {
      if (type.getLabel().equals(val)) {
        return type;
      }
    }
    return null;
  }

  public static BannerType ofCode(Integer dbData) {
      return Arrays.stream(BannerType.values())
              .filter(v -> v.getCode().equals(dbData))
              .findAny()
              .orElse(null);
  }

}
