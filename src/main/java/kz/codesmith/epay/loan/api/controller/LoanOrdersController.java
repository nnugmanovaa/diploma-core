package kz.codesmith.epay.loan.api.controller;

import io.swagger.annotations.ApiOperation;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.core.shared.controller.qualifier.ApiPageable;
import kz.codesmith.epay.loan.api.model.orders.OrderDto;
import kz.codesmith.epay.loan.api.service.ILoanOrdersService;
import kz.codesmith.epay.loan.api.service.IReportExcelService;
import kz.codesmith.epay.loan.api.service.IReportService;
import kz.codesmith.epay.loan.api.service.impl.excel.ReportDto;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Logged
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class LoanOrdersController {

  private final ILoanOrdersService ordersServices;

  @Qualifier("excelReportService")
  @Autowired
  private IReportExcelService excelreportService;

  @Qualifier("csvReportService")
  @Autowired
  private IReportService csvreportService;

  @PreAuthorize("hasAnyAuthority('MFO_ADMIN', 'ADMIN', 'CLIENT_USER')")
  @ApiPageable
  @ApiOperation(
      value = "Retrieves orders",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<OrderDto>> getOrders(
      @RequestParam @NotNull @DateTimeFormat(iso = ISO.DATE) LocalDate startDate,
      @RequestParam @NotNull @DateTimeFormat(iso = ISO.DATE) LocalDate endDate,
      @RequestParam(required = false) Integer orderId,
      @ApiIgnore Pageable pageRequest
  ) {
    return ResponseEntity.ok(ordersServices.getOrdersByUserOwner(
        startDate,
        endDate,
        orderId,
        pageRequest
    ));
  }

  @PreAuthorize("hasAnyAuthority('MFO_ADMIN', 'ADMIN', 'CLIENT_USER')")
  @ApiOperation(
      value = "Retrieve order by ID.",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<OrderDto> getOrder(@PathVariable("id") @NotNull Integer orderId) {
    return ResponseEntity.ok(ordersServices.getOrderByUserOwner(orderId));
  }

  @PreAuthorize("hasAnyAuthority('MFO_ADMIN', 'ADMIN', 'CLIENT_USER')")
  @ApiOperation(
      value = "Retrieve loan debtor form report by ID.",
      produces = MediaType.APPLICATION_PDF_VALUE
  )
  @GetMapping(value = "/loan-debtor-form/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<Resource> getLoanDebtorFormPdf(
      @PathVariable("id") @NotNull Integer orderId
  ) {
    var content = ordersServices.getLoanDebtorFormPdf(orderId);
    var resource = new InputStreamResource(new ByteArrayInputStream(content));
    return ResponseEntity.ok()
        .contentLength(content.length)
        .contentType(MediaType.APPLICATION_PDF)
        .body(resource);
  }

  @PreAuthorize("hasAuthority('CLIENT_USER')")
  @ApiOperation(
      value = "Approve order by ID.",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PutMapping(value = "/{id}/approve", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> approveOrder(@PathVariable("id") @NotNull Integer orderId) {
    ordersServices.approveOrder(orderId);
    return ResponseEntity.ok().build();
  }

  @ApiOperation(
      value = "Get order contract document",
      produces = MediaType.APPLICATION_PDF_VALUE
  )
  @GetMapping(
      path = "/{id}/contract",
      produces = MediaType.APPLICATION_PDF_VALUE
  )
  public ResponseEntity<Resource> getOrderContractDocument(
      @PathVariable("id") @NotNull Integer orderId
  ) {
    var content = ordersServices.getOrderContractDocument(orderId);
    var resource = new InputStreamResource(new ByteArrayInputStream(content));
    return ResponseEntity.ok()
        .contentLength(content.length)
        .contentType(MediaType.APPLICATION_PDF)
        .body(resource);
  }

  @ApiOperation(
      value = "Retrieves orders as csv file basing on logged user",
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
  )
  @GetMapping(
      path = "/csv",
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
  )
  public ResponseEntity<byte[]> getOrdersCsv(
      @RequestParam @NotNull @DateTimeFormat(iso = ISO.DATE) LocalDate startDate,
      @RequestParam @NotNull @DateTimeFormat(iso = ISO.DATE) LocalDate endDate,
      @RequestParam(required = false) Integer orderId
  ) {

    byte[] ordersByArray = csvreportService.getReport(startDate, endDate, orderId);

    String filename = "orders_" + startDate.toString() + "_" + endDate.toString() + ".csv";
    ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
        .filename(filename, StandardCharsets.UTF_8)
        .build();

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .contentLength(ordersByArray.length)
        .cacheControl(CacheControl.noCache())
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
        .body(ordersByArray);
  }

  @ApiOperation(value = "Retrieves orders as excel file basing on logged user")
  @PostMapping(path = "/excel")
  public ResponseEntity<byte[]> getOrdersExcel(@NotNull @RequestBody ReportDto reportDto) {
    byte[] ordersByArr = excelreportService.getReport(reportDto);
    String filename = String.format("%s_%s.xlsx",
        reportDto.getStartDate().toString(),
        reportDto.getEndDate().toString());

    if (Objects.isNull(ordersByArr)) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .contentLength(ordersByArr.length)
          .cacheControl(CacheControl.noCache())
          .header("Content-disposition", "attachment; filename=" + filename)
          .body(ordersByArr);
    }
  }
}
