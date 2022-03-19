package kz.codesmith.epay.loan.api.diploma.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class GetReportResult implements Serializable {
    @JacksonXmlProperty(localName = "CigResult")
    private CigResult cigResult;

    @JacksonXmlProperty(localName = "CigResultError")
    private CigResultError cigResultError;
}
