package kz.codesmith.epay.loan.api.model.pkb.ws;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApplicationReportDto implements Serializable {

  private static final long serialVersionUID = 7702L;
  private String requestId;
  private String credtinfoid;
  private LocalDateTime dateApplication;
  private String iin;
  private String lastname;
  private String firstname;
  private String fathername;
  private String dateofbirth;
  private String kdnScore;
  private String debt;
  private String income;
  private IncomesServiceResponseDto incomesServiceResponse;
  private ReportCrtrV2Dto incomesResultCrtrV2;
  private String flag1;
  private String flag2;
}
