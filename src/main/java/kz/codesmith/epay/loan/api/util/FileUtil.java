package kz.codesmith.epay.loan.api.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class FileUtil {

  private static final String TEMP_DIR = "temp";

  public static File base64StringToImageFile(String base64Data, Path filePath) throws IOException {
    var imageBytes = Base64.getDecoder().decode(base64Data);
    Files.write(filePath, imageBytes);

    return filePath.toFile();
  }

  public static File base64MimeStringToImageFile(String base64Data, Path filePath)
      throws IOException {
    var imageBytes = Base64.getMimeDecoder().decode(base64Data);
    Files.write(filePath, imageBytes);

    return filePath.toFile();
  }

  public static Path createTempFileWithTimeStamp(String subDir, String fileName, String suffix)
      throws IOException {
    var now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
    if (!subDir.startsWith("/")) {
      subDir = "/" + subDir;
    }
    Files.createDirectories(Path.of(TEMP_DIR + subDir));
    var tempFilePath = String.format("%s/%s-%s.%s", TEMP_DIR, fileName, now, suffix);
    return Files.createFile(Path.of(tempFilePath));
  }

  public static Path createTempFile(String subDir, String fileName) throws IOException {
    if (!subDir.startsWith("/")) {
      subDir = "/" + subDir;
    }
    Files.createDirectories(Path.of(TEMP_DIR + subDir));
    var tempFilePath = String.format("%s/%s", TEMP_DIR, fileName);
    return Files.createFile(Path.of(tempFilePath));
  }
}
