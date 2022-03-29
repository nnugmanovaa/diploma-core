package kz.codesmith.epay.loan.api.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import kz.codesmith.epay.loan.api.model.orders.OrderState;

public class ProcessConstants {

  public static final String EMISSION_PROCESS_NAME = "electronic-money-emission-process";
  public static final String SA_TO_SP_TRANSFER_PROCESS_NAME = "sub-agent-to-points-transfer";
  public static final String SP_TO_SA_TRANSFER_PROCESS_NAME = "point-to-sub-agent-transfer";
  public static final String SA_TO_AGENT_TRANSFER_PROCESS_NAME =
      "sa-to-agent-em-transfer";
  public static final String SA_PAYMENT_PROCESS_NAME = "payment-via-sa-or-sp-process";
  public static final String OPERATOR_MERCHANT_ADVANCE_PAYMENT_PROCESS_NAME =
      "merchant-advance-payment-process";
  public static final String SA_PAYMENT_ROLLBACK_PROCESS_NAME = "sa-payment-rollback-process";
  public static final String COMMISSION_PAYMENT_PROCESS_NAME = "commission-payment-process";
  public static final String BANK_SIMPLE_PAYMENT_ORDER_PROCESS_NAME =
      "bank-simple-payment-order-process";
  public static final String EMISSION_ROLLBACK_PROCESS_NAME =
      "electronic-money-emission-rollback-process";
  public static final String USER_PASSWORD_RECOVERY_PROCESS_NAME = "user-password-recovery-process";
  public static final String CLIENT_DIRECT_PAYMENT_PROCESS_NAME = "client-direct-payment-process";
  public static final String CLIENT_TUP_ADVNCD_PROCESS =
      "subscriber-wallet-top-up-process-advanced";
  public static final String CLIENT_TUP_DIRECT_PROCESS = "subscriber-wallet-top-up-process-direct";
  public static final String CLIENT_WALLET_CASHOUT_PROCESS_NAME = "client-wallet-cashout-process";
  public static final String AGENT_TO_MERCH_PROCESS = "agent-to-merch-transfer-process";


  public static final String SUBSCRIBER_WALLET_TOP_UP_PROCESS_NAME =
      "subscriber-wallet-topup";
  public static final String SUBSCRIBER_W2W_PROCESS_NAME = "subscriber-wallet-2-wallet";

  public static final String ORDER_REF_PROCESS_VAR_NAME = "orderRef";
  public static final String EXTERNAL_PAYLOAD_VAR_NAME = "extPayload";
  public static final String MESSAGE_PROCESS_HEADER_NAME = "message";
  public static final String DOCUMENT_TYPE_PROCESS_HEADER_NAME = "docType";

  public static final String BAD_ORDER_ID_ERROR_EVENT = "BAD_ORDER_ID_ERROR";

  public static final String OTP_CHECK_SUCCESS_EVENT = "OTP_CHECK_SUCCESS";
  public static final String OTP_CHECK_FAIL_EVENT = "OTP_CHECK_FAIL";

  public static final String PROCESS_SYSTEM_USERNAME = "process";
  public static final String DOCUMENT_ID_PROCESS_VAR_NAME = "documentsId";
  public static final String RESERVATION_ID_PROCESS_VAR_NAME = "accountReservationsId";

  public static final String ACCOUNT_RESERVATIONS_PROCESS_VAR_NAME = "accountReservations";
  public static final String RESERVATIONS_DTO_PREFIX_PROCESS_VAR_NAME = "reservation_";


  public static final String PROCESS_DATA_MAP_KEY = "process_init";

  public static final String BAD_ORDER_ID_ERROR = "BAD_ORDER_ID_ERROR";


  public static final String DOC_REF_PROCESS_VAR_NAME = "documentRef";
  public static final String STORNO_REF_PROCESS_VAR_NAME = "stornoRef";

  public static final String CMSN_PAY_OPERATION_TYPE = "CMSN_PAY";
  public static final String EM_EMISSION_OPERATION_TYPE = "EM_EMSN";
  public static final String SA_TO_SP_OPERATION_TYPE = "SA_TO_SP";
  public static final String SA_TO_AGENT_OPERATON_TYPE = "SA_TO_A";
  public static final String CLN_WAL_TUP_OPERATION_TYPE = "CLN_W_TUP";
  public static final String CLN_WAL2WAL_OPERATION_TYPE = "CLN_W2W";
  public static final String SP_TO_SA_OPERATION_TYPE = "SP_TO_SA";
  public static final String SA_PAYMENT_OPERATION_TYPE = "SA_PAY";
  public static final String OPER_MERCH_ADVNC_OPERATION_TYPE = "O_M_ADVNC";
  public static final String SA_PAYMENT_ROLLBACK_OPERATION_TYPE = "SA_PAY_RBK";
  public static final String EMISSION_ROLLBACK_OPERATION_TYPE = "EMSN_RBK";
  public static final String CLN_TO_MERCH_PAYMENT_OPERATION_TYPE = "CLN_W_PAY";
  public static final String CLN_WALLET_CASH_OUT_OPERATION_TYPE = "CLN_W_CO";
  public static final String AGN_M_TNS_TRANSFER_OPERATION_TYPE = "AGN_M_TNS";

  public static final List<String> paymentOrderTypes = Collections.singletonList(
      ProcessConstants.SA_PAYMENT_OPERATION_TYPE
  );

  public static final List<String> reportOrderTypes = List.of(
      ProcessConstants.SA_PAYMENT_OPERATION_TYPE,
      ProcessConstants.SA_PAYMENT_ROLLBACK_OPERATION_TYPE,
      ProcessConstants.CLN_TO_MERCH_PAYMENT_OPERATION_TYPE
  );

  public static final List<String> walletOrderTypes = List.of(
      ProcessConstants.CLN_WAL_TUP_OPERATION_TYPE,
      ProcessConstants.CLN_WAL2WAL_OPERATION_TYPE,
      ProcessConstants.CLN_WALLET_CASH_OUT_OPERATION_TYPE,
      ProcessConstants.CLN_TO_MERCH_PAYMENT_OPERATION_TYPE
  );

  public static final List<String> paymentOrderTypesStat = List.of(
      ProcessConstants.SA_PAYMENT_OPERATION_TYPE,
      ProcessConstants.CLN_TO_MERCH_PAYMENT_OPERATION_TYPE,
      ProcessConstants.CLN_WALLET_CASH_OUT_OPERATION_TYPE,
      ProcessConstants.CLN_WAL_TUP_OPERATION_TYPE
  );

  public static final List<String> extendedSearchProcessConstants = List.of(
      ProcessConstants.SA_PAYMENT_OPERATION_TYPE,
      ProcessConstants.CLN_TO_MERCH_PAYMENT_OPERATION_TYPE,
      ProcessConstants.CLN_WALLET_CASH_OUT_OPERATION_TYPE,
      ProcessConstants.CLN_WAL_TUP_OPERATION_TYPE,
      ProcessConstants.CLN_WAL2WAL_OPERATION_TYPE
  );

  public static final List<String> operationTypesForStats = List.of(
      ProcessConstants.SA_PAYMENT_OPERATION_TYPE,
      ProcessConstants.CLN_TO_MERCH_PAYMENT_OPERATION_TYPE
  );

  public static final List<String> clientOrderTypesStat = List.of(
      ProcessConstants.CLN_TO_MERCH_PAYMENT_OPERATION_TYPE,
      ProcessConstants.CLN_WALLET_CASH_OUT_OPERATION_TYPE,
      ProcessConstants.CLN_WAL_TUP_OPERATION_TYPE
  );
  public static final List<String> walletOperTypesWithoutCommission = List.of(
      ProcessConstants.CLN_WAL_TUP_OPERATION_TYPE,
      ProcessConstants.CLN_WAL2WAL_OPERATION_TYPE
  );


  public static final String PROCESS_DATA_TOTAL_AMOUNT_KEY = "amount";
  public static final String PROCESS_DATA_IS_FULL_ROLLBACK = "isFullRollback";
  public static final String PROCESS_DATA_AMOUNTS_KEY = "amounts";
  public static final String PROCESS_DATA_BILL_AMOUNT_KEY = "billAmount";
  public static final String ORDER_CREATED_PROCESS_VAR_NAME = "isOrderCreated";
  public static final String ORDER_DOES_NOT_EXIST = "ORDER_NOT_CREATED";
  public static final String APPROVE_ORDER_EVENT = "APPROVED_BY_ACCOUNTANT_EVENT";
  public static final String REJECT_ORDER_EVENT = "REJECTED_BY_ACCOUNTANT_EVENT";

  public static final String GATEWAY_RESPONSE_NAME = "gw_response";
  public static final String SIMPLE_COUNTER_PROCESS_VAR_NAME = "counter";
  public static final String ERRORS_DETAILS_PROCESS_VAR_NAME = "errorsDetails";
  public static final String MERCHANT_PAYMENT_ERROR_EVENT = "MERCHANT_PAYMENT_ERROR";


  public static final String BANK_PAYMENT_ORDER_CREATE_ERROR_EVENT =
      "BANK_PAYMENT_ORDER_CREATE_ERROR";

  public static final String ORDER_PROCESSED_BY_BANK_EVENT = "PROCESSED_BY_BANK_EVENT";
  public static final String X_REQUEST_ID_NAME = "xrequestId";
  public static final String ROLLBACK_ORDER_REF_PROCESS_VAR_NAME = "rollbackOrderRef";
  public static final String INITIAL_ORDER_REF_PROCESS_VAR_NAME = "initialOrderRef";
  public static final String PROCESS_DATA_DESTINATION_KEY = "paymentDestination";
  public static final String ORDER_INITIALIZATION_EVENT = "ORDER_INITIALIZATION_EVENT";

  public static final String EXT_REF_TIME_VAR_NAME = "extRefTime";
  public static final String CLRNG_RESPONSE_DATA_VAR_NAME = "clearingResponseData";
  public static final String CLRNG_DATA_VAR_NAME = "clearingData";
  public static final String ECOM_STATUS_VAR_NAME = "ecomStatusResponse";

  public static final String OTP_TIMEOUT_PROPERTY = "otp.timeout.seconds";

  public static final String CORRECT_OTP_EVENT_NAME = "CORRECT_OTP_EVENT";
  public static final String INCORRECT_OTP_EVENT_NAME = "INCORRECT_OTP_EVENT";
  public static final String NEW_PASSWORD_EVENT_NAME = "NEW_PASSWORD_EVENT";
  public static final String BAD_USER_ERROR = "BAD_USER_ERROR";
  public static final String PASSWORD_VAR_NAME = "password";
  public static final String AGENT_ID_VAR_NAME = "agentId";
  public static final String USER_DTO_VAR_NAME = "userDto";

  public static final String USER_VALIDATION_ATTEMPTS_PROPERTY = "user.validation.attempts";
  public static final String OTP_LIMIT_EXCEEDED_EVENT_NAME = "OTP_LIMIT_EXCEEDED";
  public static final String OTP_WINDOW_CLOSED_EVENT_NAME = "OTP_WINDOW_CLOSED";

  public static final String ORDER_EXPIRED_EVENT = "ORDER_EXPIRED_EVENT";
  public static final String IN_PROCESS_EVENT = "IN_PROCESS_EVENT";

  public static final String ECOM_PAYMENT_RESPONSE_VAR_NAME = "ecomResponsePaymentBody";
  public static final String GATEWAY_CODE_VAR_NAME = "gatewayCode";
  public static final String SERVICE_FIELDS = "serviceFields";

  public static final String CLEARING_PROCESSING_ERROR_EVENT = "CLEARING_PROCESSING_ERROR";
  public static final String BANK_CHECK_STATUS_ERROR_EVENT = "BANK_CHECK_STATUS_ERROR";

  public static final String P2P_STATUS_VAR_NAME = "p2pStatusResponse";
  public static final String P2P_PAYMENT_RESPONSE_VAR_NAME = "p2pResponsePaymentBody";

  public static final String IS_COMPLEX_SERVICE = "complex";

  public static final String NOTIFICATION_BALANCE_FIELD = "balance";
  public static final String NOTIFICATION_OWNER_ID_FIELD = "ownerId";

  public static final String PROCEDURE_NEW_ORDER_PAUSE = "NEW_ORDER_PAUSE";
  public static final String SUCCESS_RETURN_URL = "successReturnUrl";
  public static final String FAILURE_RETURN_URL = "failureReturnUrl";
}
