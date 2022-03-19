package kz.codesmith.epay.loan.api.diploma.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import java.io.Serializable;
import lombok.Data;

@Data
public class ErrorMessage implements Serializable {
    @JacksonXmlProperty(isAttribute = true)
    private String code;

    @JacksonXmlText
    private String message;
}
