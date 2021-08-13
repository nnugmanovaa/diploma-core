package kz.codesmith.epay.loan.api.service;

import kz.codesmith.epay.loan.api.model.cashout.CardCashoutRequestDto;
import kz.codesmith.epay.loan.api.model.halyk.HalykCardCashoutResponseDto;

public interface ICashoutService {

  void initCashoutToWallet(Integer orderId);

  HalykCardCashoutResponseDto initHalykEpayCashoutToCard(CardCashoutRequestDto request);

  void acceptHalykBankCallbackRequest(String requestXml);
}
