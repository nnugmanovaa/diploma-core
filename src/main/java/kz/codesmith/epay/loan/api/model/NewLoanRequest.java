package kz.codesmith.epay.loan.api.model;

import javax.validation.constraints.NotBlank;
import kz.codesmith.springboot.validators.iin.Iin;
import lombok.Data;

@Data
public class NewLoanRequest {
    @NotBlank(message = "error.empty-iin")
    @Iin(message = "{error.invalid-iin}")
    private String iin;

    @NotBlank(message = "{error.empty-firstname}")
    private String firstName;
    @NotBlank(message = "{error.empty-lastname}")
    private String lastName;

    private String patronymic;
}
