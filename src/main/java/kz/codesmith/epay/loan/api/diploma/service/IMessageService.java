package kz.codesmith.epay.loan.api.diploma.service;

import kz.codesmith.epay.loan.api.diploma.model.payout.PayoutUpdateStateEventDto;

public interface IMessageService {
  void fireLoanStatusGetEvent(PayoutUpdateStateEventDto eventDto, String routingKey);
}
