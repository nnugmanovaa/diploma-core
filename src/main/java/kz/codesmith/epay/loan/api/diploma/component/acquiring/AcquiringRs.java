package kz.codesmith.epay.loan.api.diploma.component.acquiring;

import java.time.LocalDate;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import kz.codesmith.epay.loan.api.diploma.model.payout.PayoutRequestDto;
import kz.codesmith.epay.loan.api.model.acquiring.PaymentDto;
import org.springframework.web.bind.annotation.RequestBody;

@Path("/gw")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface AcquiringRs {

  @GET
  @Path(value = "/payments")
  Response getStatus(
      @QueryParam(value = "ordersId") String orderId,
      @QueryParam(value = "orderDate") LocalDate orderDate,
      @QueryParam(value = "uuid") String extReference
  );

  @POST
  @Path(value = "/payments/cards/payout")
  Response payout(
      @RequestBody PayoutRequestDto payoutRequestDto
  );

  @POST
  @Path(value = "/payments/cards/charge")
  Response init(
      @RequestBody PaymentDto dto
  );
}
