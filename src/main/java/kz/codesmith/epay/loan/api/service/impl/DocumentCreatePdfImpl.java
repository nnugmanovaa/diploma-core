package kz.codesmith.epay.loan.api.service.impl;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import kz.codesmith.epay.loan.api.model.documents.LoanDebtorFormPdfConstants;
import kz.codesmith.epay.loan.api.model.documents.LoanDebtorFormPdfDto;
import kz.codesmith.epay.loan.api.service.IDocumentCreatePdf;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentCreatePdfImpl implements IDocumentCreatePdf {

  public static final String FONT_COURIER_NEW = "fonts/arial.ttf";
  public static final String FONT_ENCODING = "Identity-H";
  public static final int FONT_SIZE_11 = 11;
  public static final int FONT_SIZE_13 = 13;

  @Override
  public byte[] createLoanDebtorFormPdf(LoanDebtorFormPdfDto dto) {

    Font font11 = FontFactory.getFont(
        FONT_COURIER_NEW, FONT_ENCODING, BaseFont.EMBEDDED, FONT_SIZE_11);
    Font font13 = FontFactory.getFont(
        FONT_COURIER_NEW, FONT_ENCODING, BaseFont.EMBEDDED, FONT_SIZE_13);
    byte[] pdfAsBytes = new byte[0];
    Document document = new Document(PageSize.A4);

    try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
      PdfWriter.getInstance(document, os);
      document.open();

      addParagraph(LoanDebtorFormPdfConstants.MFO_INFO, font11,
          Element.ALIGN_RIGHT, document);
      document.add(Chunk.NEWLINE);

      addParagraph(LoanDebtorFormPdfConstants.HEADER, font13,
          Element.ALIGN_CENTER, document);
      document.add(Chunk.NEWLINE);

      addParagraph(String.format(LoanDebtorFormPdfConstants.CURRENT_DATE,
          DateTimeFormatter.ofPattern("dd.MM.yyyy").format(LocalDate.now())),
          font11, Element.ALIGN_CENTER, document);
      document.add(Chunk.NEWLINE);

      PdfPTable table = new PdfPTable(2);
      table.setWidthPercentage(100);

      Map<String, String> params = new LinkedHashMap<>();
      params.put(LoanDebtorFormPdfConstants.TABLE_HEADER_COL1,
          LoanDebtorFormPdfConstants.TABLE_HEADER_COL2);
      params.put(LoanDebtorFormPdfConstants.LOAN_PURPOSE, dto.getLoanPurpose());
      params.put(LoanDebtorFormPdfConstants.LOAN_SECURITY, dto.getLoanSecurity());
      params.put(LoanDebtorFormPdfConstants.FIO, dto.getFio());
      params.put(LoanDebtorFormPdfConstants.IIN, dto.getIin());
      params.put(LoanDebtorFormPdfConstants.BIRTHDATE, dto.getBirthdate());
      params.put(LoanDebtorFormPdfConstants.GENDER, dto.getGender());
      params.put(LoanDebtorFormPdfConstants.ID_TYPE, dto.getIdType());
      params.put(LoanDebtorFormPdfConstants.ID_INFO, dto.getIdInfo());
      params.put(LoanDebtorFormPdfConstants.MARITAL_STATUS, dto.getMaritalStatus());
      params.put(LoanDebtorFormPdfConstants.NUMBER_OF_KIDS, dto.getNumberOfKids());
      params.put(LoanDebtorFormPdfConstants.WORKPLACE, dto.getWorkplace());
      params.put(LoanDebtorFormPdfConstants.WORK_POSITION, dto.getWorkPosition());
      params.put(LoanDebtorFormPdfConstants.INCOME, dto.getIncome());
      params.put(LoanDebtorFormPdfConstants.NUMBER, dto.getNumbersInString());
      params.put(LoanDebtorFormPdfConstants.EMAIL, dto.getEmail());
      params.put(LoanDebtorFormPdfConstants.ADDRESS, dto.getAddress());
      params.put(LoanDebtorFormPdfConstants.LOAN_METHOD, dto.getLoanMethod());

      params.forEach((k, v) -> {
        addTableRow(k, v, table, font11);
      });
      document.add(table);

      document.newPage();
      PdfPTable annotationsTable = new PdfPTable(2);
      annotationsTable.setWidthPercentage(100);
      annotationsTable.setWidths(new int[]{3, 2});
      addTableRow(
          String.format(
              LoanDebtorFormPdfConstants.ANNOTATIONS_RU, dto.getLoanMethodRuKzPair().getFirst()),
          String.format(
              LoanDebtorFormPdfConstants.ANNOTATIONS_KZ, dto.getLoanMethodRuKzPair().getSecond()),
          annotationsTable,
          font11
      );
      document.add(annotationsTable);
      document.add(Chunk.NEWLINE);
      document.add(Chunk.NEWLINE);
      document.add(Chunk.NEWLINE);
      addParagraph(LoanDebtorFormPdfConstants.SIGNATURE,
          font11, Element.ALIGN_BOTTOM, document);

      document.close();
      pdfAsBytes = os.toByteArray();
    } catch (Exception ex) {
      log.error(ex.getMessage());
    }
    document.close();
    return pdfAsBytes;
  }

  private void addTableRow(String col1, String col2, PdfPTable table, Font font) {
    table.addCell(getCell(col1, font));
    table.addCell(getCell(col2, font));
  }

  public void addParagraph(String raw, Font font, int alignment, Document document)
      throws DocumentException {
    Paragraph preface = new Paragraph();
    preface.setAlignment(alignment);
    preface.setFont(font);
    preface.add(raw);
    document.add(preface);
  }

  public PdfPCell getCell(String value, Font font) {
    PdfPCell cell = new PdfPCell();
    cell.setPadding(5);
    Paragraph p = new Paragraph(value, font);
    cell.addElement(p);
    return cell;
  }
}
