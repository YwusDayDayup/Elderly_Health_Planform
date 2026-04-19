package com.example.service.external;

import java.math.BigDecimal;

public interface PaymentGateway {

  String pay(String orderNo, BigDecimal amount);

  String refund(String orderNo, BigDecimal amount);
}
