package kz.codesmith.epay.loan.api.diploma.model.kdn;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class ResponseData implements Serializable {
    @JacksonXmlProperty(localName = "data")
    private kz.codesmith.epay.loan.api.diploma.model.kdn.Data data;
}
