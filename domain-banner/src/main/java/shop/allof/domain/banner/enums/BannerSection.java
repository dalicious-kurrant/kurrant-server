package shop.allof.domain.banner.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BannerSection {
  TIE("띠"), HERO("히어로");

  private String label;

  BannerSection(String label) {
    this.label = label;
  }

  @JsonCreator
  public static BannerSection from(String val) {
    for (BannerSection section : BannerSection.values()) {
      if (section.getLabel().equals(val)) {
        return section;
      }
    }
    return null;
  }

  @JsonValue
  public String getLabel() {
    return this.label;
  }

}
