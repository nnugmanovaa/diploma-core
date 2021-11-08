package kz.codesmith.epay.loan.api.component;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentGenerator {
  public static final String FONT_FILE_PATH = "fonts/Lora.ttf";
  private final PebbleEngine pebbleEngine;

  public byte[] generatePdf(String template, Map<String, Object> values) throws IOException {
    var html = renderPebbleToHtml(template, values);
    return htmlToPdf(html);
  }

  private byte[] htmlToPdf(String html) throws IOException {
    var fontInputStream = new ClassPathResource(FONT_FILE_PATH).getInputStream();
    var renderedPdfBytes = new ByteArrayOutputStream();
    var builder = new PdfRendererBuilder()
        .useFastMode()
        .useFont(() -> fontInputStream, "Lora")
        .withHtmlContent(html, "/")
        .toStream(renderedPdfBytes);

    builder.run();
    renderedPdfBytes.close();
    return renderedPdfBytes.toByteArray();
  }

  private String renderPebbleToHtml(String template, Map<String, Object> values)
      throws IOException {
    var pebbleTemplate = pebbleEngine.getTemplate(template);
    var stringWriter = new StringWriter();
    pebbleTemplate.evaluate(stringWriter, values);

    return stringWriter.toString();
  }
}
