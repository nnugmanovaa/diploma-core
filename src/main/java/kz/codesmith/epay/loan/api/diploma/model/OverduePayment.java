package kz.codesmith.epay.loan.api.diploma.model;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OverduePayment implements Serializable {
    private LocalDate date;
    private int overdueDays;
    private String fine;
    private String penalty;
}
