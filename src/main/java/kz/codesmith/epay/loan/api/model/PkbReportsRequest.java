package kz.codesmith.epay.loan.api.model;

import kz.codesmith.epay.loan.api.model.pkb.kdn.KdnRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PkbReportsRequest {
  private String iin;
  private KdnRequest kdnRequest;
}
