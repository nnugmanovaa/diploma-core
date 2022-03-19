package kz.codesmith.epay.loan.api.diploma.model.kdn;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class IncomesResult implements Serializable {
    @JacksonXmlProperty(localName = "responseData")
    private ResponseData responseData;
}
