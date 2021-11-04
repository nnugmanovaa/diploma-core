package kz.codesmith.epay.loan.api.service.impl.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import kz.codesmith.epay.loan.api.model.orders.OrderReportRecord;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelHelper {

  public static byte[] ordersToExcel(List<OrderReportRecord> source) {
    try (Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();) {
      Sheet sheet = workbook.createSheet("Orders");
      Row headerRow = sheet.createRow(0);
      Cell cell;
      for (int col = 0; col < ExcelConstants.ORDER_HEADERS.length; col++) {
        cell = headerRow.createCell(col);
        cell.setCellValue(ExcelConstants.ORDER_HEADERS[col]);
      }

      for (int i = 0; i < source.size(); i++) {
        final Row dateRow = sheet.createRow(i + 1);
        setRowColumnValue(dateRow, 0, source.get(i).getOrderId());
        setRowColumnValue(dateRow, 1, source.get(i).getStatus());
        setRowColumnValue(dateRow, 2, source.get(i).getInsertedTime());
        setRowColumnValue(dateRow, 3, source.get(i).getUpdatedTime());
        setRowColumnValue(dateRow, 4, source.get(i).getIin());
        setRowColumnValue(dateRow, 5, source.get(i).getClient());
        setRowColumnValue(dateRow, 6, source.get(i).getLoanAmount());
        setRowColumnValue(dateRow, 7, source.get(i).getLoanPeriodMonths());
        setRowColumnValue(dateRow, 8, source.get(i).getLoanProduct());
        setRowColumnValue(dateRow, 9, source.get(i).getPersonalInfoDto().getBirthDate());
        setRowColumnValue(dateRow, 10, source.get(i).getPersonalInfoDto().getGender());
        setRowColumnValue(dateRow, 11, source.get(i).getMsisdn());
        setRowColumnValue(dateRow, 12, source.get(i).getPersonalInfoDto()
            .getResidenceAddress().getCity());
        setRowColumnValue(dateRow, 13, source.get(i).getPersonalInfoDto().getEmployment());
        setRowColumnValue(dateRow, 14, source.get(i).getPersonalInfoDto().getMaritalStatus());
        setRowColumnValue(dateRow, 15, source.get(i).getPersonalInfoDto().getNumberOfKids());
        setRowColumnValue(dateRow, 16, source.get(i).getPersonalInfoDto().getMonthlyIncome());
        setRowColumnValue(dateRow, 17, source.get(i).getPersonalInfoDto()
            .getAdditionalMonthlyIncome());
        setRowColumnValue(dateRow, 18, source.get(i).getScoringInfo().getKdn());
        setRowColumnValue(dateRow, 19, source.get(i).getScoringInfo().getDebt());
        setRowColumnValue(dateRow, 20, source.get(i).getScoringInfo().getScore());
        setRowColumnValue(dateRow, 21, source.get(i).getScoringInfo().getIncome());
        setRowColumnValue(dateRow, 22, source.get(i).getScoringInfo().getNewKdn());
        setRowColumnValue(dateRow, 23, source.get(i).getScoringInfo().getBadRate());
        setRowColumnValue(dateRow, 24, source.get(i).getScoringInfo()
            .isHasOverdue() ? "Есть" : "Нет");
        setRowColumnValue(dateRow, 25, source.get(i).getScoringInfo().getMaxPaymentSum());
        setRowColumnValue(dateRow, 26, source.get(i).getScoringInfo().getMaxContractSum());
      }
      for (int i = 0; i < 25; i++) {
        sheet.autoSizeColumn(i);
      }
      workbook.write(out);
      return out.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
    }
  }

  private static void setRowColumnValue(final Row dataRow, int index, Integer value) {
    dataRow.createCell(index).setCellValue(value != null ? value : 0);
  }

  private static void setRowColumnValue(final Row dataRow, int index, Double value) {
    dataRow.createCell(index).setCellValue(value != null ? value : 0);
  }

  private static void setRowColumnValue(final Row dataRow, int index, String value) {
    dataRow.createCell(index).setCellValue(value != null ? value : "");
  }

  private static void setRowColumnValue(final Row dataRow, int index, LocalDateTime value) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String formattedString = value.format(formatter);
    dataRow.createCell(index).setCellValue(value != null ? formattedString : "");
  }

  private static void setRowColumnValue(final Row dataRow, int index, OrderState value) {
    dataRow.createCell(index).setCellValue(value != null ? value.toString() : "");
  }

  private static void setRowColumnValue(final Row dataRow, int index, BigDecimal value) {
    dataRow.createCell(index).setCellValue(value != null ? value.doubleValue() : 0);
  }

  private static void setRowColumnValue(final Row dataRow, int index, LocalDate value) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String formattedString = value.format(formatter);
    dataRow.createCell(index).setCellValue(value != null ? formattedString : "");
  }

  private static void setRowColumnValue(final Row dataRow, int index, Boolean value) {
    dataRow.createCell(index).setCellValue(value);
  }

}
