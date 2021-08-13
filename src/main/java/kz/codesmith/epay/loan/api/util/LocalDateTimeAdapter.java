package kz.codesmith.epay.loan.api.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

  @Override
  public LocalDateTime unmarshal(String v) throws Exception {
    String val = v.trim();

    if (val.matches("\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}.\\d{3}")) {
      return LocalDateTime.parse(val, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

    } else if (val.matches("\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}")) {
      return LocalDateTime.parse(val, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    } else if (val.matches("(19|20)[0-9][0-9]-(0[0-9]|1[0-2])-(0[1-9]|([12]"
        + "[0-9]|3[01]))T([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]")) {
      return LocalDateTime.parse(val, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

    } else if (val.matches("\\d{4}-\\d{2}-\\d{2}")) {
      LocalDate localDate = LocalDate.parse(val, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      return LocalDateTime.of(localDate, LocalDateTime.MIN.toLocalTime());

    } else if (val.matches("\\d{2}.\\d{2}.\\d{4}")) {
      LocalDate localDate = LocalDate.parse(val, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
      return LocalDateTime.of(localDate, LocalDateTime.MIN.toLocalTime());
    }

    return null;
  }

  @Override
  public String marshal(LocalDateTime v) throws Exception {
    if (v != null) {
      return v.toString();
    } else {
      return null;
    }
  }
}
