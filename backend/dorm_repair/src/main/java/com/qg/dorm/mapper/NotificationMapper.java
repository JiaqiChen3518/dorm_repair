package com.qg.dorm.mapper;

import com.qg.dorm.entity.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper {

    /**
     * 插入通知
     * @param notification
     * @return
     */
    @Insert("INSERT INTO notification (user_id, type, title, content, is_read, related_id, create_time) " +
            "VALUES (#{userId}, #{type}, #{title}, #{content}, 0, #{relatedId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notification notification);

    /**
     * 根据用户ID查询通知
     * @param userId
     * @return
     */
    @Select("SELECT * FROM notification WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Notification> selectByUserId(Long userId);

    /**
     * 根据用户ID查询未读通知
     * @param userId
     * @return
     */
    @Select("SELECT * FROM notification WHERE user_id = #{userId} AND is_read = 0 ORDER BY create_time DESC")
    List<Notification> selectUnreadByUserId(Long userId);

    /**
     * 标记通知为已读
     * @param id
     * @return
     */
    @Update("UPDATE notification SET is_read = 1 WHERE id = #{id}")
    int markAsRead(Long id);

    /**
     * 标记用户所有通知为已读
     * @param userId
     * @return
     */
    @Update("UPDATE notification SET is_read = 1 WHERE user_id = #{userId}")
    int markAllAsRead(Long userId);

    /**
     * 查询用户未读通知数量
     * @param userId
     * @return
     */
    @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId} AND is_read = 0")
    int countUnread(Long userId);
}
