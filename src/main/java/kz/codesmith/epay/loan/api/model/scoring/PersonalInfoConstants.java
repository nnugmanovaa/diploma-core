package kz.codesmith.epay.loan.api.model.scoring;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class PersonalInfoConstants {
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
}
