package kz.codesmith.epay.loan.api.controller.pub;

import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import kz.codesmith.epay.loan.api.model.CalculationDto;
import kz.codesmith.epay.loan.api.service.IMfoCoreService;
import kz.codesmith.logger.Logged;
import kz.payintech.LoanSchedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Logged
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicMfoController {

  private final IMfoCoreService mfoCoreService;

  @ApiOperation(
      value = "Get Loan Schedule Calculation",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(path = "/loan/schedule/calculation", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getLoanScheduleCalculation(
      @RequestBody @Valid CalculationDto calculationDto
  ) {
    return ResponseEntity.ok(mfoCoreService.getLoanScheduleCalculation(
        calculationDto.getAmount(),
        calculationDto.getLoanMonthPeriod(),
        calculationDto.getLoanInterestRate(),
        calculationDto.getCreditProduct(),
        calculationDto.getLoanType()
    ));
  }

}
