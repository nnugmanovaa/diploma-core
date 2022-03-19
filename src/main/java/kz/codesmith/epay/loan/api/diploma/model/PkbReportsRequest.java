package kz.codesmith.epay.loan.api.diploma.model;

import kz.codesmith.epay.loan.api.diploma.model.kdn.KdnRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PkbReportsRequest {
  private String iin;
  private KdnRequest kdnRequest;
}
