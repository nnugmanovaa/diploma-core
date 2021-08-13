package kz.codesmith.epay.loan.api.component;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import kz.codesmith.epay.loan.api.model.cashout.InitClientWalletTopUpDto;
import kz.codesmith.epay.loan.api.model.cashout.OrderStatusDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(path = "/agent/wallets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/agent/wallets")
public interface IAgentClientTransferRs {

  @POST
  @Path(("/topup"))
  @PostMapping("/topup")
  OrderStatusDto initWalletTopUp(@RequestBody @Valid InitClientWalletTopUpDto dto);
}
