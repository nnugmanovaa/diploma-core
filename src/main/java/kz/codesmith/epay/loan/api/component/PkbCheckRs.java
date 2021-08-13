package kz.codesmith.epay.loan.api.component;

import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_XML)
public interface PkbCheckRs {

  @GET
  @Path("/{iin}.xml")
  String getCustomerInfoByIin(@PathParam("iin") @NotBlank String iin);
}
