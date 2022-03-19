package kz.codesmith.epay.loan.api.diploma.model.kdn;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class KdnReqResponse implements Serializable {
    @JacksonXmlProperty(namespace = "ns2", localName = "return")
    private Return aReturn;
}
