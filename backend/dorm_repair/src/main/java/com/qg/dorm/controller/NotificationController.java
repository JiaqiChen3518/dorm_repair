package com.qg.dorm.controller;

import com.qg.dorm.common.Result;
import com.qg.dorm.entity.Notification;
import com.qg.dorm.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/myNotifications")
    public Result<List<Notification>> getMyNotifications(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return notificationService.getMyNotifications(userId);
    }

    @GetMapping("/unread")
    public Result<List<Notification>> getUnreadNotifications(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return notificationService.getUnreadNotifications(userId);
    }

    @GetMapping("/unreadCount")
    public Result<Integer> getUnreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return notificationService.getUnreadCount(userId);
    }

    @PostMapping("/markAsRead/{notificationId}")
    public Result<Void> markAsRead(@PathVariable Long notificationId) {
        return notificationService.markAsRead(notificationId);
    }

    @PostMapping("/markAllAsRead")
    public Result<Void> markAllAsRead(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return notificationService.markAllAsRead(userId);
    }
}
