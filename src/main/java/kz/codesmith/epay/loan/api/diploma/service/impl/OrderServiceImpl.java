package kz.codesmith.epay.loan.api.diploma.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import kz.codesmith.epay.loan.api.diploma.service.IOrdersService;
import kz.codesmith.epay.loan.api.domain.orders.OrderEntity;
import kz.codesmith.epay.loan.api.model.exception.MfoGeneralApiException;
import kz.codesmith.epay.loan.api.model.orders.OrderDto;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import kz.codesmith.epay.loan.api.repository.LoanOrdersRepository;
import kz.codesmith.epay.security.model.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrdersService {
  private final LoanOrdersRepository loanOrdersRepository;
  private final UserContextHolder userContextHolder;
  private final ModelMapper mapper;

  @Override
  public Page<OrderDto> getOrdersByUserOwner(LocalDate startDate, LocalDate endDate,
      Integer orderId, List<OrderState> states, Pageable pageRequest) {
    return loanOrdersRepository.findAllByInsertedTimeIsBetweenAndClientAndState(
        startDate.atStartOfDay(),
        endDate.atTime(LocalTime.MAX),
        orderId,
        userContextHolder.getContext().getOwnerId(),
        states,
        pageRequest
    ).map(o -> mapper.map(o, OrderDto.class));
  }

  @Override
  public OrderEntity getOrderByUserOwner(Integer orderId) {
    return loanOrdersRepository.findByOrderId(orderId).orElse(null);
  }

  @Transactional
  @Override
  public OrderDto approveOrder(Integer orderId) {
    var order = getOrderByUserOwner(orderId);
    order.setStatus(OrderState.CONFIRMED);
    return mapper.map(order, OrderDto.class);
  }
}
