
package com.example.mapper;

import com.example.dto.StatisticsDTO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StatisticsMapper {

  @Select("select count(1) from t_user")
  Long totalUsers();

  @Select("select count(1) from t_user where date(create_time) = curdate()")
  Long todayNewUsers();

  @Select("select role as name, count(distinct user_id) as count from t_user_role group by role order by count desc")
  List<StatisticsDTO.NameCountRow> roleDistribution();

  @Select("select count(1) from t_file")
  Long totalFiles();

  @Select("select ifnull(sum(size), 0) from t_file")
  Long totalFileSize();

  @Select("select ifnull(content_type, 'unknown') as name, count(1) as count from t_file group by content_type order by count desc")
  List<StatisticsDTO.NameCountRow> fileTypeDistribution();

  @Select("select count(1) from t_msg")
  Long totalMsgs();

  @Select("select count(1) from t_msg where read_flag = 0")
  Long totalUnreadMsgs();

  @Select("select date_format(create_time, '%Y-%m-%d') as day, count(1) as count from t_user where create_time >= date_sub(curdate(), interval #{days} day) group by date_format(create_time, '%Y-%m-%d') order by day")
  List<StatisticsDTO.DayCountRow> userTrend(@Param("days") int days);

  @Select("select date_format(create_time, '%Y-%m-%d') as day, count(1) as count from t_file where create_time >= date_sub(curdate(), interval #{days} day) group by date_format(create_time, '%Y-%m-%d') order by day")
  List<StatisticsDTO.DayCountRow> fileTrend(@Param("days") int days);

  @Select("select date_format(create_time, '%Y-%m-%d') as day, count(1) as count from t_msg where create_time >= date_sub(curdate(), interval #{days} day) group by date_format(create_time, '%Y-%m-%d') order by day")
  List<StatisticsDTO.DayCountRow> msgTrend(@Param("days") int days);
}
