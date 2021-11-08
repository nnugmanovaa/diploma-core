package kz.codesmith.epay.loan.api.component.sms;

public interface SmsSender {

  void sendSms(String msisdn, String text, boolean attachTail);
}
