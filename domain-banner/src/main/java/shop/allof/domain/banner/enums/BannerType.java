package shop.allof.domain.banner.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BannerType {
  BUY("구매"), SELL("판매");

  private String label;

  BannerType(String label) {
    this.label = label;
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

  @JsonValue
  public String getLabel() {
    return this.label;
  }

}
