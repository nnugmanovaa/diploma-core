package kz.codesmith.epay.loan.api.diploma.model.kdn;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class ApplicationReport implements Serializable {
    @JacksonXmlProperty(namespace = "ns2", localName = "RequestId")
    private String requestId;

    @JacksonXmlProperty(namespace = "ns2", localName = "dateApplication")
    private String dateApplication;

    @JacksonXmlProperty(namespace = "ns2", localName = "IIN")
    private String iin;

    @JacksonXmlProperty(namespace = "ns2", localName = "kdn_score")
    private Double kdnScore;

    @JacksonXmlProperty(namespace = "ns2", localName = "debt")
    private Double debt;

    @JacksonXmlProperty(namespace = "ns2", localName = "income")
    private Double income;

    @JacksonXmlProperty(namespace = "ns2", localName = "incomesResultCrtrV2")
    private IncomesResultCrtrV2 incomesResultCrtrV2;

    @JacksonXmlProperty(namespace = "errorCode", localName = "errorCode")
    private String errorCode;

    @JacksonXmlProperty(namespace = "errorMessage", localName = "errorMessage")
    private String errorMessage;
}
