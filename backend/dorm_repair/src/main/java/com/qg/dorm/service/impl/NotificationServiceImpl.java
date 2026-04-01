package com.qg.dorm.service.impl;

import com.qg.dorm.common.Result;
import com.qg.dorm.entity.Notification;
import com.qg.dorm.mapper.NotificationMapper;
import com.qg.dorm.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public Result<Void> createNotification(Long userId, Integer type, String title, String content, Long relatedId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedId(relatedId);
        notification.setIsRead(0);

        notificationMapper.insert(notification);
        return Result.success();
    }

    @Override
    public Result<List<Notification>> getMyNotifications(Long userId) {
        List<Notification> notifications = notificationMapper.selectByUserId(userId);
        return Result.success(notifications);
    }

    @Override
    public Result<List<Notification>> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationMapper.selectUnreadByUserId(userId);
        return Result.success(notifications);
    }

    @Override
    public Result<Void> markAsRead(Long notificationId) {
        notificationMapper.markAsRead(notificationId);
        return Result.success();
    }

    @Override
    public Result<Void> markAllAsRead(Long userId) {
        if (userId == null) {
            return Result.error("用户未登录");
        }
        notificationMapper.markAllAsRead(userId);
        return Result.success();
    }

    @Override
    public Result<Integer> getUnreadCount(Long userId) {
        int count = notificationMapper.countUnread(userId);
        return Result.success(count);
    }
}
