package com.example.service.external;

import com.example.common.constant.CareConstants;
import org.springframework.stereotype.Component;

@Component
public class MockSmsGateway implements SmsGateway {

  private final ExternalCallLogService externalCallLogService;

  public MockSmsGateway(ExternalCallLogService externalCallLogService) {
    this.externalCallLogService = externalCallLogService;
  }

  @Override
  public String send(String phone, String templateCode, String content, String bizId) {
    String req = "{\"phone\":\"" + phone + "\",\"templateCode\":\"" + templateCode + "\"}";
    String resp = "{\"status\":\"SENT\",\"phone\":\"" + phone + "\",\"content\":\"" + content + "\"}";
    externalCallLogService.log(CareConstants.CALL_SMS, templateCode, "MESSAGE", bizId, req, resp, true);
    return resp;
  }
}
