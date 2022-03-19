package kz.codesmith.epay.loan.api.diploma.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.io.Serializable;
import kz.codesmith.epay.loan.api.diploma.model.kdn.KdnReqResponse;
import lombok.Data;

@Data
public class Body implements Serializable {
    @JacksonXmlProperty(localName = "GetReportResponse")
    private GetReportResponse reportResponse;

    @JacksonXmlProperty(namespace = "ns2", localName = "kdnReqResponse")
    private KdnReqResponse kdnReqResponse;
}
