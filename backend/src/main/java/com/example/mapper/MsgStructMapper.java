
package com.example.mapper;

import com.example.dto.MsgDTO;
import com.example.entity.MsgEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface MsgStructMapper {

  @Mappings({
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "createTime", ignore = true),
      @Mapping(target = "updateTime", ignore = true),
      @Mapping(target = "senderId", source = "senderId"),
      @Mapping(target = "readFlag", constant = "0"),
      @Mapping(target = "readTime", ignore = true)
  })
  MsgEntity toEntity(MsgDTO.SendReq req, Long senderId);

  @Mappings({
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "createTime", ignore = true),
      @Mapping(target = "updateTime", ignore = true),
      @Mapping(target = "senderId", source = "senderId"),
      @Mapping(target = "receiverId", source = "receiverId"),
      @Mapping(target = "readFlag", constant = "0"),
      @Mapping(target = "readTime", ignore = true)
  })
  MsgEntity toEntity(MsgDTO.BroadcastReq req, Long senderId, Long receiverId);

  MsgDTO.MsgVO toMsgVO(MsgEntity entity);

  MsgDTO.AdminMsgVO toAdminMsgVO(MsgEntity entity);

  
  default MsgDTO.UnreadCountVO toUnreadCountVO(Long count) {
    return new MsgDTO.UnreadCountVO(count == null ? 0 : count);
  }

  List<MsgDTO.MsgVO> toMsgVOList(List<MsgEntity> list);

  List<MsgDTO.AdminMsgVO> toAdminMsgVOList(List<MsgEntity> list);
}
