package kz.codesmith.epay.loan.api.diploma.model.kdn;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@lombok.Data
public class Data implements Serializable {
    @JacksonXmlProperty(localName = "responseCode")
    private String responseCode;

    @JacksonXmlProperty(localName = "requestNumber")
    private String requestNumber;

    @JacksonXmlProperty(localName = "requestIin")
    private String requestIin;

    @JacksonXmlProperty(localName = "responseDate")
    private String responseDate;

    @JacksonXmlProperty(localName = "responseNumber")
    private String responseNumber;

    @JacksonXmlElementWrapper(useWrapping = false, localName = "deductionsDetailed")
    private List<DeductionsDetailed> deductionsDetailed = new ArrayList<>();
}
