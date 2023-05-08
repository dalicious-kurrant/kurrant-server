package co.dalicious.domain.banner.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum BannerSection {
  ADVERTISEMENT("광고", 1),
  NOTICE("공지", 2);

  private final String label;
  private final Integer code;

  BannerSection(String label, Integer code) {
    this.label = label;
    this.code = code;
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

  public static BannerSection ofCode(Integer code) {
    return Arrays.stream(BannerSection.values())
            .filter(v -> v.getCode().equals(code))
            .findAny()
            .orElse(null);
  }

  @JsonValue
  public String getLabel() {
    return this.label;
  }

}
