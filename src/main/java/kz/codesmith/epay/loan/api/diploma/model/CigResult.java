package kz.codesmith.epay.loan.api.diploma.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class CigResult implements Serializable {
    @JacksonXmlProperty(localName = "DateTime")
    private String dateTime;
    @JacksonXmlProperty(localName = "Result")
    private Result result;
}
