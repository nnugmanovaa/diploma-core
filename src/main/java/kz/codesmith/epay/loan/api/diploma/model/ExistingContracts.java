package kz.codesmith.epay.loan.api.diploma.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class ExistingContracts implements Serializable {
    @JacksonXmlProperty(isAttribute = true)
    private String stitle;

    @JacksonXmlProperty(localName = "PaymentsCalendar")
    private PaymentsCalendar paymentsCalendar;

    @JacksonXmlProperty(localName = "Contract")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Contract> contracts;
}
