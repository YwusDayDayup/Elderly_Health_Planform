package com.example.service.external;

public interface SmsGateway {

  String send(String phone, String templateCode, String content, String bizId);
}
