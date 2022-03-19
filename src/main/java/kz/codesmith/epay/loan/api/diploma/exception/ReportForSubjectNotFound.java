package kz.codesmith.epay.loan.api.diploma.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ReportForSubjectNotFound extends RuntimeException{
    private String message;

    public ReportForSubjectNotFound(String msg) {
        super(msg);
        this.message = msg;
    }
}
