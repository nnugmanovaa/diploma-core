package kz.codesmith.epay.loan.api.diploma.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class GetReportResponse implements Serializable {
    @JacksonXmlProperty(localName = "GetReportResult")
    private GetReportResult reportResult;
}
