package kz.codesmith.epay.loan.api.controller;

import io.swagger.annotations.ApiOperation;
import java.math.BigDecimal;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import kz.codesmith.epay.loan.api.service.IMfoCoreService;
import kz.codesmith.logger.Logged;
import kz.payintech.ListLoanMethod;
import kz.payintech.LoanSchedule;
import kz.payintech.NewOrder;
import kz.payintech.ResultDataNumberDate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Logged
@RestController
@RequestMapping("/mfo")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('MFO_ADMIN', 'ADMIN', 'CLIENT_USER')")
@Validated
public class MfoCoreController {

  private final IMfoCoreService mfoCoreService;

  @ApiOperation(
      value = "Get User Orders by IIN",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @GetMapping(path = "/user/orders/{iin}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getUserOrderList(@PathVariable("iin") @NotBlank String iin) {
    return ResponseEntity.ok(mfoCoreService.getUserOrderList(iin));
  }

  @ApiOperation(
      value = "Get Sum Payments Schedule by IIN",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('MFO_ADMIN', 'ADMIN', 'CLIENT_USER')")
  @GetMapping(path = "/sum/payments/schedule/{iin}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getSummPaymentSchedule(@PathVariable("iin") @NotBlank String iin) {
    return ResponseEntity.ok(mfoCoreService.getSummPaymentSchedule(iin));
  }

  @ApiOperation(
      value = "Get User Contracts by IIN",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('MFO_ADMIN', 'ADMIN', 'CLIENT_USER')")
  @GetMapping(path = "/user/contracts/{iin}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getUserContractsList(@PathVariable("iin") @NotBlank String iin) {
    return ResponseEntity.ok(mfoCoreService.getUserContractsList(iin));
  }

  @ApiOperation(
      value = "Get Total Debt by IIN",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('MFO_ADMIN', 'ADMIN', 'CLIENT_USER')")
  @GetMapping(path = "/total/debt/{iin}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getTotalDebt(@PathVariable("iin") @NotBlank String iin) {
    return ResponseEntity.ok(mfoCoreService.getTotalDebt(iin));
  }

  @SneakyThrows
  @ApiOperation(
      value = "Get new contract document",
      produces = MediaType.APPLICATION_PDF_VALUE
  )
  @PostMapping(
      path = "/user/contracts/{loanInterestRate}",
      produces = MediaType.APPLICATION_PDF_VALUE
  )
  public ResponseEntity<byte[]> getNewContract(
      @RequestBody @Valid ResultDataNumberDate dto,
      @PathVariable("loanInterestRate") float loanInterestRate
  ) {
    var headers = new HttpHeaders();
    var filename = dto.getNumber() + "_" + dto.getDateTime().toString() + ".pdf";
    headers.add("Content-Disposition", "attachment; filename=" + filename);
    var response = mfoCoreService.getNewContract(dto, loanInterestRate);
    return ResponseEntity.ok()
        .headers(headers)
        .contentType(MediaType.APPLICATION_PDF)
        .body(Base64.decodeBase64(response.getContract()));
  }

  @ApiOperation(
      value = "Create new User Order",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(path = "/user/orders", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getNewContract(@RequestBody @Valid NewOrder dto) {
    return ResponseEntity.ok(mfoCoreService.getNewOrder(dto));
  }

  /**
   * Расчет кредитного займа.
   *
   * @param creditProduct    кредитный продукт
   * @param amount           сумма займа
   * @param loanMonthPeriod  период займа в месяцах
   * @param loanInterestRate ставка по займу
   * @param loanType         тип платежей
   * @return {@link LoanSchedule}
   */
  @ApiOperation(
      value = "Get Loan Schedule Calculation",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(path = "/loan/schedule/calculation", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getLoanScheduleCalculation(
      @RequestParam(name = "creditProduct") @NotBlank String creditProduct,
      @RequestParam(name = "amount") @NotNull @Positive BigDecimal amount,
      @RequestParam(name = "loanMonthPeriod") @NotNull Integer loanMonthPeriod,
      @RequestParam(name = "loanInterestRate") @NotNull float loanInterestRate,
      @RequestParam(name = "loanType") @NotNull ListLoanMethod loanType
  ) {
    return ResponseEntity.ok(mfoCoreService.getLoanScheduleCalculation(
        amount,
        loanMonthPeriod,
        loanInterestRate,
        creditProduct,
        loanType
    ));
  }

  @ApiOperation(
      value = "Accept Contract terms",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(path = "/user/contracts/accept", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> sendBorrowerSignature(@RequestBody @Valid ResultDataNumberDate dto) {
    mfoCoreService.sendBorrowerSignature(dto);
    return ResponseEntity.ok().build();
  }

}
