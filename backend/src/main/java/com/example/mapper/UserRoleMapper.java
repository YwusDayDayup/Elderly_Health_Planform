
package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.UserRoleEntity;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRoleEntity> {

  
  @Select("select role from t_user_role where user_id = #{userId}")
  List<String> selectRolesByUserId(Long userId);

  
  @Select("SELECT user_id FROM t_user_role WHERE role = #{role}")
  List<Long> selectUserIdsByRole(@Param("role") String role);

  
  @Insert({"<script>",
      "INSERT INTO t_user_role (user_id, role, create_time, update_time)",
      "VALUES",
      "<foreach collection='list' item='item' separator=','>",
      "(#{item.userId}, #{item.role}, NOW(3), NOW(3))",
      "</foreach>",
      "</script>"})
  int insertBatch(@Param("list") List<UserRoleEntity> list);

  
  @Delete("DELETE FROM t_user_role WHERE user_id = #{userId}")
  int deleteByUserId(@Param("userId") Long userId);
}
