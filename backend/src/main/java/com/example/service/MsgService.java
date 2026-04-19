
package com.example.service;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.constant.GlobalConstants;
import com.example.common.context.UserContextHolder;
import com.example.common.exception.BizException;
import com.example.common.model.PageQuery;
import com.example.common.result.PageResult;
import com.example.dto.MsgDTO;
import com.example.entity.MsgEntity;
import com.example.entity.UserEntity;
import com.example.mapper.MsgMapper;
import com.example.mapper.MsgStructMapper;
import com.example.mapper.UserMapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MsgService extends ServiceImpl<MsgMapper, MsgEntity> {

  private static final int READ_NO = 0;

  private final MsgMapper msgMapper;
  private final UserMapper userMapper;
  private final MsgStructMapper msgStructMapper;

  public MsgService(MsgMapper msgMapper, UserMapper userMapper,
      MsgStructMapper msgStructMapper) {
    this.msgMapper = msgMapper;
    this.userMapper = userMapper;
    this.msgStructMapper = msgStructMapper;
  }

  
  @Transactional
  public void send(MsgDTO.SendReq req) {
    Long userId = UserContextHolder.requireUserId();
    save(msgStructMapper.toEntity(req, userId));
  }

  
  @Transactional(readOnly = true)
  public List<MsgDTO.MsgVO> myList(Integer readFlag) {
    Long userId = UserContextHolder.requireUserId();
    List<MsgEntity> list = msgMapper.selectList(new LambdaQueryWrapper<MsgEntity>()
        .eq(MsgEntity::getReceiverId, userId)
        .eq(readFlag != null, MsgEntity::getReadFlag, readFlag)
        .orderByDesc(MsgEntity::getId));
    return msgStructMapper.toMsgVOList(list);
  }

  
  @Transactional(readOnly = true)
  public MsgDTO.UnreadCountVO unreadCount() {
    Long userId = UserContextHolder.requireUserId();
    Long count = msgMapper.selectCount(new LambdaQueryWrapper<MsgEntity>()
        .eq(MsgEntity::getReceiverId, userId)
        .eq(MsgEntity::getReadFlag, READ_NO));
    return msgStructMapper.toUnreadCountVO(count);
  }

  
  @Transactional
  public void markRead(Long msgId) {
    Long userId = UserContextHolder.requireUserId();
    int updated = msgMapper.markReadIfOwner(msgId, userId, LocalDateTime.now());
    if (updated > 0) {
      return;
    }
    MsgEntity msg = getById(msgId);
    Assert.notNull(msg, () -> new BizException(BizException.Code.NOT_FOUND, "消息不存在"));
    if (msg.getReceiverId() == null || !msg.getReceiverId().equals(userId)) {
      throw new BizException(BizException.Code.FORBIDDEN, "无权限操作该消息");
    }
  }

  
  @Transactional
  public void markReadAll() {
    msgMapper.markReadAllByReceiver(UserContextHolder.requireUserId(), LocalDateTime.now());
  }

  
  @Transactional(readOnly = true)
  public MsgDTO.MsgVO myDetail(Long msgId) {
    Long userId = UserContextHolder.requireUserId();
    MsgEntity msg = getById(msgId);
    Assert.notNull(msg, () -> new BizException(BizException.Code.NOT_FOUND, "消息不存在"));
    if (msg.getReceiverId() == null || !msg.getReceiverId().equals(userId)) {
      throw new BizException(BizException.Code.FORBIDDEN, "无权限查看该消息");
    }
    return msgStructMapper.toMsgVO(msg);
  }

  
  @Transactional
  public void adminBroadcast(MsgDTO.BroadcastReq req) {
    Long userId = UserContextHolder.requireUserId();
    List<UserEntity> users = userMapper.selectList(new LambdaQueryWrapper<UserEntity>()
        .eq(UserEntity::getStatus, GlobalConstants.STATUS_ENABLED));
    if (users.isEmpty()) {
      return;
    }
    List<MsgEntity> list = users.stream().map(u -> {
      return msgStructMapper.toEntity(req, userId, u.getId());
    }).toList();
    msgMapper.insertBatch(list);
  }

  
  @Transactional(readOnly = true)
  public PageResult<MsgDTO.AdminMsgVO> adminPage(MsgDTO.AdminPageReq req) {
    IPage<MsgDTO.AdminMsgVO> mpjPage = new Page<>(PageQuery.pageNo(req.pageNo()), PageQuery.pageSize(req.pageSize()));
    IPage<MsgDTO.AdminMsgVO> result = msgMapper.selectJoinPage(mpjPage, MsgDTO.AdminMsgVO.class,
        new MPJLambdaWrapper<MsgEntity>()
            .selectAll(MsgEntity.class)
            .selectAs(UserEntity::getNickname, "receiverNickname")
            .leftJoin(UserEntity.class, UserEntity::getId, MsgEntity::getReceiverId)
            .eq(req.receiverId() != null, MsgEntity::getReceiverId, req.receiverId())
            .eq(StringUtils.hasText(req.msgType()), MsgEntity::getMsgType, req.msgType())
            .eq(req.readFlag() != null, MsgEntity::getReadFlag, req.readFlag())
            .ge(req.startTime() != null, MsgEntity::getCreateTime, req.startTime())
            .le(req.endTime() != null, MsgEntity::getCreateTime, req.endTime())
            .orderByDesc(MsgEntity::getId)
    );
    return PageResult.of(result.getTotal(), result.getRecords());
  }

  
  @Transactional
  public void adminDelete(Long id) {
    removeById(id);
  }

}
