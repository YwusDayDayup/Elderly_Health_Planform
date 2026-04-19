
package com.example.controller;

import com.example.common.result.Result;
import com.example.common.web.MpCrudController;
import com.example.dto.MsgDTO;
import com.example.entity.MsgEntity;
import com.example.mapper.MsgMapper;
import com.example.service.MsgService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/messages")
public class MsgController extends MpCrudController<MsgEntity, MsgMapper> {

  private final MsgService msgService;

  public MsgController(MsgMapper mapper, MsgService msgService) {
    super(mapper);
    this.msgService = msgService;
  }

  @Override
  @PostMapping
  @Transactional
  public Result<Long> create(@RequestBody MsgEntity entity) {
    MsgDTO.SendReq req = new MsgDTO.SendReq(
        entity.getReceiverId(),
        entity.getMsgType(),
        entity.getTitle(),
        entity.getContent(),
        entity.getBizId());
    msgService.send(req);
    return Result.ok(null);
  }

  @PostMapping("/broadcast")
  public Result<Void> broadcast(@RequestBody MsgDTO.BroadcastReq req) {
    msgService.adminBroadcast(req);
    return Result.ok(null);
  }
}
