package com.qg.dorm.service;

import com.qg.dorm.common.Result;
import com.qg.dorm.entity.Notification;

import java.util.List;

public interface NotificationService {

    /**
     * 创建通知
     * @param userId
     * @param type
     * @param title
     * @param content
     * @param relatedId
     * @return
     */
    Result<Void> createNotification(Long userId, Integer type, String title, String content, Long relatedId);

    /**
     * 查询我的通知
     * @param userId
     * @return
     */
    Result<List<Notification>> getMyNotifications(Long userId);

    /**
     * 查询我的未读通知
     * @param userId
     * @return
     */
    Result<List<Notification>> getUnreadNotifications(Long userId);

    /**
     * 标记通知为已读
     * @param notificationId
     * @return
     */
    Result<Void> markAsRead(Long notificationId);

    /**
     * 标记所有通知为已读
     * @param userId
     * @return
     */
    Result<Void> markAllAsRead(Long userId);

    /**
     * 查询我的未读通知数量
     * @param userId
     * @return
     */
    Result<Integer> getUnreadCount(Long userId);
}
