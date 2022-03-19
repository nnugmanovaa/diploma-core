package kz.codesmith.epay.loan.api.diploma;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import kz.codesmith.epay.loan.api.diploma.exception.PkbReportRequestFailed;
import kz.codesmith.epay.loan.api.diploma.exception.ReportForSubjectNotFound;
import kz.codesmith.epay.loan.api.diploma.model.CigResult;
import kz.codesmith.epay.loan.api.diploma.model.Contract;
import kz.codesmith.epay.loan.api.diploma.model.Envelope;
import kz.codesmith.epay.loan.api.diploma.model.OverduePayment;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StandardReportResultParser {

    public static final String NOT_FOUND_ERR_CODE = "1108";

    public CigResult parseRoot(String report) throws JsonProcessingException {
        var xmlMapper = new XmlMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        var parsedValue = xmlMapper.readValue(report, Envelope.class);
        return parsedValue.getBody().getReportResponse().getReportResult().getCigResult();
    }

    public static CigResult parse(String reportResult) {
        var xmlMapper = new XmlMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            var parsedValue = xmlMapper.readValue(reportResult, Envelope.class);
            var err = parsedValue.getBody().getReportResponse().getReportResult().getCigResultError();
            if (err != null) {
                var errCode = err.getErrorMessage().getCode();
                var errMsg = err.getErrorMessage().getMessage();

                log.warn("[{}] {}", errCode, errMsg);
                if(NOT_FOUND_ERR_CODE.equals(errCode.trim())){
                    throw new ReportForSubjectNotFound(errCode + " - " + errMsg);
                } else {
                    throw new PkbReportRequestFailed(errCode + " - " + errMsg);
                }
            }

            return parsedValue.getBody().getReportResponse().getReportResult().getCigResult();
        } catch (JsonProcessingException e) {
            log.error("Failed to parse PKB response: " + reportResult);
            throw new PkbReportRequestFailed("Failed to parse PKB response");
        }
    }

    public List<OverduePayment> getAllOverduePayments(CigResult data) {
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

    private List<OverduePayment> extractOverduePayments(List<Contract> contracts) {
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

    public boolean overdueInLastPeriodExist(
            long periodInMonths,
            long overdueDays,
            List<OverduePayment> overduePayments
    ) {
        var since = LocalDate.now().minusMonths(periodInMonths).withDayOfMonth(1);
        return overduePayments.stream().anyMatch( overduePayment ->
                        overduePayment.getDate().isAfter(since)
                        && overduePayment.getOverdueDays() >= overdueDays
                );
    }

    public List<OverduePayment> overdueInLastPeriod(
            long periodInMonths,
            long overdueDays,
            List<OverduePayment> overduePayments
    ) {
        var since = LocalDate.now().minusMonths(periodInMonths).withDayOfMonth(1);
        return overduePayments.stream().filter( overduePayment ->
                overduePayment.getDate().isAfter(since)
                        && overduePayment.getOverdueDays() >= overdueDays
        ).collect(Collectors.toList());
    }
}
