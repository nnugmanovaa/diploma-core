package kz.codesmith.epay.loan.api.controller;

import io.swagger.annotations.ApiOperation;
import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.core.shared.controller.qualifier.ApiPageable;
import kz.codesmith.epay.loan.api.diploma.service.IOrdersService;
import kz.codesmith.epay.loan.api.model.orders.OrderDto;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Logged
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {
  private final IOrdersService ordersService;

  @PreAuthorize("hasAuthority('CLIENT_USER')")
  @ApiPageable
  @ApiOperation(
      value = "Retrieves orders",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<OrderDto>> getOrders(
      @RequestParam @NotNull @DateTimeFormat(iso = ISO.DATE) LocalDate startDate,
      @RequestParam @NotNull @DateTimeFormat(iso = ISO.DATE) LocalDate endDate,
      @RequestParam(required = false) Integer orderId,
      @RequestParam(required = false) List<OrderState> states,
      @ApiIgnore Pageable pageRequest
  ) {
    return ResponseEntity.ok(ordersService.getOrdersByUserOwner(
        startDate,
        endDate,
        orderId,
        states,
        pageRequest
    ));
  }
}
