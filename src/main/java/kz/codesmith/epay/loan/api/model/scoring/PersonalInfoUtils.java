package kz.codesmith.epay.loan.api.model.scoring;

import java.math.BigDecimal;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class PersonalInfoUtils {
  public static final String education = "Магистратура, 2-ое высшее, Ученая степегь и пр.";

  public static final String employment = "Безработный";

  public static final String typeOfWork = "Другое";

  public static final String workPosition = "Нет";

  public static final String employer = "Нет";

  public static final BigDecimal monthlyIncome = BigDecimal.ONE;

  public static final BigDecimal additionalMonthlyIncome = BigDecimal.ONE;

  public static final Integer workExperience = 0;

  public static final String workPhoneNum = "71111111111";

  public static final String maritalStatus = "Не женат/ Не замужем";

  public static final String gender = "Другое";

  @NotNull
  @Positive
  public static final Integer numberOfKids = 0;

  public static void fillEmptyFormData(PersonalInfoDto personalInfoDto) {
    if (Objects.isNull(personalInfoDto.getEducation())) {
      personalInfoDto.setEducation(education);
    }
    if (Objects.isNull(personalInfoDto.getEmployment())) {
      personalInfoDto.setEmployment(employment);
    }
    if (Objects.isNull(personalInfoDto.getTypeOfWork())) {
      personalInfoDto.setTypeOfWork(typeOfWork);
    }
    if (Objects.isNull(personalInfoDto.getWorkPosition())) {
      personalInfoDto.setWorkPosition(workPosition);
    }
    if (Objects.isNull(personalInfoDto.getEmployer())) {
      personalInfoDto.setEmployer(employer);
    }
    if (Objects.isNull(personalInfoDto.getMonthlyIncome())) {
      personalInfoDto.setMonthlyIncome(monthlyIncome);
    }
    if (Objects.isNull(personalInfoDto.getAdditionalMonthlyIncome())) {
      personalInfoDto.setAdditionalMonthlyIncome(additionalMonthlyIncome);
    }
    if (Objects.isNull(personalInfoDto.getWorkExperience())) {
      personalInfoDto.setWorkExperience(workExperience);
    }
    if (Objects.isNull(personalInfoDto.getWorkPhoneNum())) {
      personalInfoDto.setWorkPhoneNum(workPhoneNum);
    }
    if (Objects.isNull(personalInfoDto.getMaritalStatus())) {
      personalInfoDto.setMaritalStatus(maritalStatus);
    }
    if (Objects.isNull(personalInfoDto.getGender())) {
      personalInfoDto.setGender(gender);
    }
    if (Objects.isNull(personalInfoDto.getNumberOfKids())) {
      personalInfoDto.setNumberOfKids(numberOfKids);
    }
  }
}
