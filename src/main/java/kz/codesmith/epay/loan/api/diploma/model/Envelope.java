package kz.codesmith.epay.loan.api.diploma.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.io.Serializable;
import lombok.Data;

@Data
@JacksonXmlRootElement(namespace = "S",localName = "Envelope")
public class Envelope implements Serializable {
    @JacksonXmlProperty(namespace = "S", localName = "Body")
    private Body body;
}
