package com.example.service.external;

import com.example.common.constant.CareConstants;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class MockPaymentGateway implements PaymentGateway {

  private final ExternalCallLogService externalCallLogService;

  public MockPaymentGateway(ExternalCallLogService externalCallLogService) {
    this.externalCallLogService = externalCallLogService;
  }

  @Override
  public String pay(String orderNo, BigDecimal amount) {
    String resp = "{\"status\":\"PAID\",\"orderNo\":\"" + orderNo + "\",\"amount\":" + amount + "}";
    externalCallLogService.log(CareConstants.CALL_PAYMENT, "PAY", "SERVICE_ORDER", orderNo,
        "{\"orderNo\":\"" + orderNo + "\",\"amount\":" + amount + "}", resp, true);
    return resp;
  }

  @Override
  public String refund(String orderNo, BigDecimal amount) {
    String resp = "{\"status\":\"REFUNDED\",\"orderNo\":\"" + orderNo + "\",\"amount\":" + amount + "}";
    externalCallLogService.log(CareConstants.CALL_PAYMENT, "REFUND", "SERVICE_ORDER", orderNo,
        "{\"orderNo\":\"" + orderNo + "\",\"amount\":" + amount + "}", resp, true);
    return resp;
  }
}
