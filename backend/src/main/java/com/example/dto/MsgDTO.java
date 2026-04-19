
package com.example.dto;

import com.example.common.model.PageQuery;
import java.time.LocalDateTime;

public class MsgDTO {

  public record SendReq(
      Long receiverId,
      String msgType,
      String title,
      String content,
      String bizId
  ) {
  }

  public record MarkReadReq(Long msgId) {
  }

  public record BroadcastReq(
      String msgType,
      String title,
      String content,
      String bizId
  ) {
  }

  public record AdminPageReq(
      Long pageNo,
      Long pageSize,
      Long receiverId,
      String msgType,
      Integer readFlag,
      LocalDateTime startTime,
      LocalDateTime endTime
  ) implements PageQuery {
  }

  public record AdminMsgVO(
      Long id,
      Long senderId,
      Long receiverId,
      String receiverNickname,
      String msgType,
      String title,
      String content,
      String bizId,
      Integer readFlag,
      LocalDateTime readTime,
      LocalDateTime createTime
  ) {
  }

  public record UnreadCountVO(long count) {
  }

  public record MsgVO(
      Long id,
      String msgType,
      String title,
      String content,
      String bizId,
      Integer readFlag,
      LocalDateTime readTime,
      LocalDateTime createTime
  ) {
  }
}
