
package com.example.controller;

import com.example.common.result.Result;
import com.example.dto.MsgDTO;
import com.example.service.MsgService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MsgMeController {

  private final MsgService msgService;

  public MsgMeController(MsgService msgService) {
    this.msgService = msgService;
  }

  @GetMapping("/api/me/messages")
  public Result<List<MsgDTO.MsgVO>> myList(@RequestParam(required = false) Integer readFlag) {
    return Result.ok(msgService.myList(readFlag));
  }

  @GetMapping("/api/me/messages/{id}")
  public Result<MsgDTO.MsgVO> myDetail(@PathVariable Long id) {
    return Result.ok(msgService.myDetail(id));
  }

  @GetMapping("/api/me/messages/unread-count")
  public Result<MsgDTO.UnreadCountVO> unreadCount() {
    return Result.ok(msgService.unreadCount());
  }

  @PatchMapping("/api/me/messages/{id}/read")
  public Result<Void> markRead(@PathVariable Long id) {
    msgService.markRead(id);
    return Result.ok(null);
  }

  @PostMapping("/api/me/message-read-batches")
  public Result<Void> markReadAll() {
    msgService.markReadAll();
    return Result.ok(null);
  }
}
