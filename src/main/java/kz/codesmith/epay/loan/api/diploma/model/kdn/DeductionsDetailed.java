package kz.codesmith.epay.loan.api.diploma.model.kdn;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class DeductionsDetailed implements Serializable {
    @JacksonXmlProperty(localName = "bin")
    private String bin;

    @JacksonXmlProperty(localName = "date")
    private String date;

    @JacksonXmlProperty(localName = "name")
    private String name;

    @JacksonXmlProperty(localName = "amount")
    private Double amount;
}
