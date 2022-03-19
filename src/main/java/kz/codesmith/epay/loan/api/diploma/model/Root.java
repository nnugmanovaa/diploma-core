package kz.codesmith.epay.loan.api.diploma.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class Root implements Serializable {
    @JacksonXmlProperty(localName = "Title")
    private Title title;

    @JacksonXmlProperty(localName = "ExistingContracts")
    private ExistingContracts existingContracts;

    @JacksonXmlProperty(localName = "TerminatedContracts")
    private TerminatedContracts terminatedContracts;

    @JacksonXmlProperty(localName = "WithdrawnApplications")
    private WithdrawnApplications withdrawnApplications;
}
