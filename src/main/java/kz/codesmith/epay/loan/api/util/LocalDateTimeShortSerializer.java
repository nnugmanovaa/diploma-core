package kz.codesmith.epay.loan.api.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class LocalDateTimeShortSerializer extends JsonSerializer<LocalDateTime> {

  private final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
  private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);

  @Override
  public void serialize(
      LocalDateTime localDateTime, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider)
      throws IOException {

    jsonGenerator.writeString(
        Optional.ofNullable(localDateTime).map(v -> v.atZone(ZoneId.systemDefault())
            .format(dateTimeFormatter))
            .orElse(null)
    );
  }
}
