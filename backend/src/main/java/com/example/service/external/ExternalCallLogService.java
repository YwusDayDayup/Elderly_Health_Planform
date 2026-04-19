package com.example.service.external;

import com.example.entity.ExternalCallLogEntity;
import com.example.mapper.ExternalCallLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExternalCallLogService {

  private final ExternalCallLogMapper externalCallLogMapper;

  public ExternalCallLogService(ExternalCallLogMapper externalCallLogMapper) {
    this.externalCallLogMapper = externalCallLogMapper;
  }

  @Transactional
  public void log(String providerType, String action, String bizType, String bizId,
      String reqJson, String respJson, boolean success) {
    ExternalCallLogEntity entity = new ExternalCallLogEntity();
    entity.setProviderType(providerType);
    entity.setAction(action);
    entity.setBizType(bizType);
    entity.setBizId(bizId);
    entity.setReqJson(reqJson);
    entity.setRespJson(respJson);
    entity.setSuccess(success ? 1 : 0);
    externalCallLogMapper.insert(entity);
  }
}
