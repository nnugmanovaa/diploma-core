package kz.codesmith.epay.loan.api.diploma.model.kdn;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class IncomesResultCrtrV2 implements Serializable {
    @JacksonXmlProperty(localName = "fcbStatus")
    private String fcbStatus;

    @JacksonXmlProperty(localName = "fcbMessage")
    private String fcbMessage;

    @JacksonXmlProperty(localName = "result")
    private IncomesResult result;
}
