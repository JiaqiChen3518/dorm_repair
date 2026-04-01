package com.qg.dorm.service.impl;

import com.qg.dorm.common.Result;
import com.qg.dorm.constant.OrderStatusConstant;
import com.qg.dorm.entity.RepairOrder;
import com.qg.dorm.entity.Statistics;
import com.qg.dorm.entity.User;
import com.qg.dorm.mapper.RepairOrderMapper;
import com.qg.dorm.mapper.UserMapper;
import com.qg.dorm.service.NotificationService;
import com.qg.dorm.service.RepairOrderService;
import com.qg.dorm.util.OrderNoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RepairOrderServiceImpl implements RepairOrderService {

    @Autowired
    private RepairOrderMapper repairOrderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional
    public Result<RepairOrder> createOrder(Long userId, String building, String room, Integer deviceType, Integer priority, String description, String images) {
        RepairOrder order = new RepairOrder();
        order.setOrderNo(OrderNoUtil.generateOrderNo());
        order.setUserId(userId);
        order.setBuilding(building);
        order.setRoom(room);
        order.setDeviceType(deviceType);
        order.setPriority(priority);
        order.setDescription(description);
        order.setImages(images);
        order.setStatus(OrderStatusConstant.STATUS_PENDING);

        // 插入报修单
        repairOrderMapper.insert(order);

        // 发送通知
        notificationService.createNotification(userId, 1, "报修单已创建",
                "您的报修单 " + order.getOrderNo() + " 已成功创建，等待处理", order.getId());

        return Result.success(order);
    }

    @Override
    public Result<List<RepairOrder>> getMyOrders(Long userId, Integer status) {
        List<RepairOrder> orders = repairOrderMapper.selectByUserId(userId, status);
        return Result.success(orders);
    }

    @Override
    public Result<RepairOrder> getOrderDetail(Long orderId) {
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("报修单不存在");
        }
        return Result.success(order);
    }

    @Override
    public Result<Void> cancelOrder(Long orderId, Long userId) {
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("报修单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.error("无权操作");
        }
        if (order.getStatus() != OrderStatusConstant.STATUS_PENDING) {
            return Result.error("只能取消待处理的报修单");
        }

        repairOrderMapper.cancelOrder(orderId);
        notificationService.createNotification(userId, 1, "报修单已取消",
                "您的报修单 " + order.getOrderNo() + " 已取消", orderId);
        return Result.success();
    }

    @Override
    public Result<List<RepairOrder>> getAllOrders(Integer status, String building) {
        List<RepairOrder> orders = repairOrderMapper.selectAll(status, building);
        return Result.success(orders);
    }

    @Override
    @Transactional
    public Result<Void> updateOrderStatus(Long orderId, Integer status, Long handlerId) {
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("报修单不存在");
        }

        repairOrderMapper.updateStatus(orderId, status, handlerId);

        String statusText = OrderStatusConstant.getStatusText(status);
        notificationService.createNotification(order.getUserId(), 1, "报修单状态更新",
                "您的报修单 " + order.getOrderNo() + " 状态已更新为：" + statusText, orderId);

        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> completeOrder(Long orderId, String note, Long handlerId) {
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("报修单不存在");
        }

        repairOrderMapper.completeOrder(orderId, note);
        notificationService.createNotification(order.getUserId(), 1, "报修单已完成",
                "您的报修单 " + order.getOrderNo() + " 已处理完成，请评价", orderId);

        return Result.success();
    }

    @Override
    public Result<Void> deleteOrder(Long orderId) {
        repairOrderMapper.deleteById(orderId);
        return Result.success();
    }

    @Override
    public Result<Statistics> getStatistics() {
        Statistics statistics = new Statistics();
        statistics.setTotal(repairOrderMapper.countAll());
        statistics.setPending(repairOrderMapper.countByStatus(0));
        statistics.setProcessing(repairOrderMapper.countByStatus(1));
        statistics.setCompleted(repairOrderMapper.countByStatus(2));
        return Result.success(statistics);
    }
}
