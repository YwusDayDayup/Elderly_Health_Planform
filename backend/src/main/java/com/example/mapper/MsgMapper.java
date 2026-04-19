
package com.example.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.MsgDTO;
import com.example.entity.MsgEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MsgMapper extends MPJBaseMapper<MsgEntity> {

  @Update("""
      UPDATE t_msg
      SET read_flag = 1, read_time = #{readTime}
      WHERE id = #{msgId}
        AND receiver_id = #{receiverId}
        AND read_flag = 0
      """)
  int markReadIfOwner(@Param("msgId") Long msgId,
      @Param("receiverId") Long receiverId,
      @Param("readTime") LocalDateTime readTime);

  @Update("""
      UPDATE t_msg
      SET read_flag = 1, read_time = #{readTime}
      WHERE receiver_id = #{receiverId}
        AND read_flag = 0
      """)
  int markReadAllByReceiver(@Param("receiverId") Long receiverId,
      @Param("readTime") LocalDateTime readTime);

  @Insert({"<script>",
      "INSERT INTO t_msg (sender_id, receiver_id, msg_type, title, content, biz_id, read_flag, read_time, create_time, update_time)",
      "VALUES",
      "<foreach collection='list' item='item' separator=','>",
      "(#{item.senderId}, #{item.receiverId}, #{item.msgType}, #{item.title}, #{item.content}, #{item.bizId}, #{item.readFlag}, #{item.readTime}, NOW(3), NOW(3))",
      "</foreach>",
      "</script>"})
  int insertBatch(@Param("list") List<MsgEntity> list);
}
