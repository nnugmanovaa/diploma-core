package kz.codesmith.epay.loan.api.model.scoring;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PersonalInfoDto {

  @NotBlank
  private String lastName;

  @NotBlank
  private String firstName;

  private String middleName;

  @NotNull
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate birthDate;

  @NotNull
  private DocumentDto nationalIdDocument;

  @NotNull
  private AddressDto registrationAddress;

  @NotNull
  private AddressDto residenceAddress;

  private String education;

  private String employment;

  private String typeOfWork;

  private String workPosition;

  private String employer;

  private BigDecimal monthlyIncome;

  private BigDecimal additionalMonthlyIncome;

  private Integer workExperience;

  private String workPhoneNum;

  private String maritalStatus;

  private String gender;

  @NotNull
  @Positive
  private Integer numberOfKids;

}
