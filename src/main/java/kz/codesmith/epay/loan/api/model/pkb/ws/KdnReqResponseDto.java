package kz.codesmith.epay.loan.api.model.pkb.ws;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class KdnReqResponseDto implements Serializable {

  private static final long serialVersionUID = 7702L;
  private ApplicationReportDto applicationReport;
  private String reportDate;
  private int id;
  private int errorCode;
  private String errorMessage;

}
