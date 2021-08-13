package kz.codesmith.epay.loan.api.component.acquiring;

import java.time.LocalDate;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import kz.codesmith.epay.loan.api.model.acquiring.PaymentDto;
import org.springframework.web.bind.annotation.RequestBody;

@Path("/gw/payments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface AcquiringRs {

  @GET
  @Path(value = "")
  Response getStatus(
      @QueryParam(value = "ordersId") String orderId,
      @QueryParam(value = "orderDate") LocalDate orderDate,
      @QueryParam(value = "uuid") String extReference
  );

  @POST
  @Path(value = "/cards/charge")
  Response init(
      @RequestBody PaymentDto dto
  );
}