package kz.codesmith.epay.loan.api.model.acquiring;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class IpDetailsDto {

  private String ip;

  private String hostname;

  private String continentCode;

  private String continentName;

  private String countryCode;

  private String countryName;

  private String regionCode;

  private String regionName;

  private String city;

  private String zip;

  private Long latitude;

  private Long longitude;

}

