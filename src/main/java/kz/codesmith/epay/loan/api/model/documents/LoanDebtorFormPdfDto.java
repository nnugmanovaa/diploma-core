package kz.codesmith.epay.loan.api.model.documents;

import lombok.Data;
import org.springframework.data.util.Pair;

@Data
public class LoanDebtorFormPdfDto {

  private String loanPurpose;
  private String loanSecurity;
  private String fio;
  private String iin;
  private String birthdate;
  private String gender;
  private String idType;
  private String idInfo;
  private String maritalStatus;
  private String numberOfKids;
  private String workplace;
  private String workPosition;
  private String income;
  private String numbersInString;
  private String email;
  private String address;
  private String loanMethod;
  private Pair<String, String> loanMethodRuKzPair;

}
