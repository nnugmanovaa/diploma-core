package kz.codesmith.epay.loan.api.diploma;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import kz.codesmith.epay.loan.api.diploma.model.CigResult;
import kz.codesmith.epay.loan.api.diploma.model.Contract;
import kz.codesmith.epay.loan.api.diploma.model.OverduePayment;

public class OverduesService {
  public static List<OverduePayment> getAllOverduePayments(CigResult data) {
    var existingContracts = data.getResult().getRoot().getExistingContracts().getContracts();
    var existingOverdue = extractOverduePayments(existingContracts);

    var terminatedContracts = data.getResult().getRoot().getTerminatedContracts().getContracts();
    var terminatedOverdue = extractOverduePayments(terminatedContracts);

    var withdrawnContracts = data.getResult().getRoot().getWithdrawnApplications().getContracts();
    var withdrawnOverdue = extractOverduePayments(withdrawnContracts);

    var combinedOverdue = new ArrayList<OverduePayment>();
    combinedOverdue.addAll(existingOverdue);
    combinedOverdue.addAll(terminatedOverdue);
    combinedOverdue.addAll(withdrawnOverdue);

    return combinedOverdue;
  }

  public  static  List<OverduePayment> extractOverduePayments(List<Contract> contracts) {
    var overduePayments = new ArrayList<OverduePayment>();
    if (contracts != null) {
      contracts.forEach(contract -> {
        if (contract.getPaymentsCalendar() != null) {
          contract.getPaymentsCalendar().getYears().forEach(year -> {
            year.getPayments().forEach(payment -> {
              var overdueDays = payment.getOverdueDays();
              if (!"".equals(overdueDays) && !"-".equals(overdueDays) && !"0".equals(overdueDays)) {
                overduePayments.add(
                    OverduePayment.builder()
                        .date(LocalDate.of(year.getYear(), payment.getMonth(), 1))
                        .penalty(payment.getPenalty())
                        .fine(payment.getFine())
                        .overdueDays(Integer.parseInt(overdueDays))
                        .build()
                );
              }
            });
          });
        }
      });
    }
    return overduePayments;
  }
}
