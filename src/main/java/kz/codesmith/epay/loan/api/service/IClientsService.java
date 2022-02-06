package kz.codesmith.epay.loan.api.service;

import kz.codesmith.epay.loan.api.model.ClientExistDto;

public interface IClientsService {

  void checkRequestIinSameClientIin(String iin);

  ClientExistDto checkClientExist(String clientName);
}
