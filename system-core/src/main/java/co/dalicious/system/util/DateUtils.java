package co.dalicious.system.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
  public static String toISO(Date date) {
    SimpleDateFormat sdf;
    sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    return sdf.format(date);
  }

  public static String toISO(Timestamp ts) {
    SimpleDateFormat sdf;
    sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    return sdf.format(ts);
  }

  public static String format(Date date, String formatString) {
    SimpleDateFormat sdf;
    sdf = new SimpleDateFormat(formatString);
    return sdf.format(date);
  }
}
