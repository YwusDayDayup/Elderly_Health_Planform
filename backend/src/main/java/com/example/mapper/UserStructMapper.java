
package com.example.mapper;

import com.example.dto.UserDTO;
import com.example.entity.UserEntity;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserStructMapper {

  UserDTO.ProfileVO toProfileVO(UserEntity entity);

  UserDTO.AdminUserVO toAdminUserVO(UserEntity entity);

  @Mapping(target = "role", source = "role")
  UserDTO.AdminUserDetailVO toAdminUserDetailVO(UserEntity entity, String role);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createTime", ignore = true)
  @Mapping(target = "updateTime", ignore = true)
  @Mapping(target = "passwordHash", ignore = true)
  @Mapping(target = "avatarUrl", ignore = true)
  @Mapping(target = "status", ignore = true)
  UserEntity toEntity(UserDTO.RegisterReq req);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createTime", ignore = true)
  @Mapping(target = "updateTime", ignore = true)
  @Mapping(target = "passwordHash", ignore = true)
  @Mapping(target = "avatarUrl", ignore = true)
  UserEntity toEntity(UserDTO.AdminCreateReq req);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createTime", ignore = true)
  @Mapping(target = "updateTime", ignore = true)
  @Mapping(target = "username", ignore = true)
  @Mapping(target = "passwordHash", ignore = true)
  @Mapping(target = "status", ignore = true)
  void updateByProfile(UserDTO.UpdateProfileReq req, @MappingTarget UserEntity entity);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createTime", ignore = true)
  @Mapping(target = "updateTime", ignore = true)
  @Mapping(target = "username", ignore = true)
  @Mapping(target = "passwordHash", ignore = true)
  void updateByAdmin(UserDTO.AdminUpdateReq req, @MappingTarget UserEntity entity);

  List<UserDTO.AdminUserVO> toAdminUserVOList(List<UserEntity> list);
}
